#include <stdio.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sqlite3.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <string.h>

#include "./sql.h"

#define MAX_POTHOLES 100
#define MAX_DATA_RETRIEVED 100


int insertNewPothole(sqlite3* database, char *username, double latitudine, double longitudine){
    char *put_query;
    int status_query;

    put_query = sqlite3_mprintf("INSERT INTO Potholes VALUES ('%q', %f, %f);", username, latitudine, longitudine); // %q permette di visualizzare anche gli apici

    status_query = sqlite3_exec(database, put_query, NULL, NULL, NULL);

    return status_query;
}

int getNearPotholes(sqlite3 *database, int socket, double latitudine, double longitudine, double distanza){
  char *get_query;
  const char *username;
  double lat, lon;
  int status_query;
  sqlite3_stmt *res;
  char dataRetrieved[MAX_DATA_RETRIEVED]; /*buffer*/

  get_query = sqlite3_mprintf("SELECT * FROM Potholes WHERE latitude >= %f AND latitudine <= %f AND longitude >= %f AND longitude <= %f;", latitudine-distanza, latitudine+distanza, longitudine-distanza, longitudine+distanza);
  /*PROVARE A RIFARE I PARAMETRI CON sqlite3_bind*/

  status_query = sqlite3_prepare_v2(database, get_query, strlen(get_query), &res, NULL);

  while(sqlite3_step(res) == SQLITE_ROW){

    /*get results and set them in to variables*/
    username = sqlite3_column_text(res, 0);
    lat = sqlite3_column_double(res, 1);
    lon =  sqlite3_column_double(res, 2);

    /*cleaning the buffer*/
    bzero((char*) &dataRetrieved, sizeof(dataRetrieved));

    /*sending data to client*/
    sprintf(dataRetrieved, "%s;", username);
    send(socket, dataRetrieved, 50, 0);

    sprintf(dataRetrieved, "%f;", lat);
    send(socket, dataRetrieved, 50, 0);

    sprintf(dataRetrieved, "%f\n", lon);
    send(socket, dataRetrieved, 50, 0);

    printf("Inviati i dati al client \n");
    /*controllare se si può fare tutto in un'unica sprintf e send*/
  }

  sqlite3_finalize(res);

  return 0;

}

int getAllPotholes(sqlite3 *database, int socket){
  int status_query;
  char *get_query = "SELECT * FROM Potholes";
  char *error_message = "Errore durante la query";

  status_query = sqlite3_exec(database, get_query, callback, &socket, &error_message);

  if(status_query != SQLITE_OK){
    perror("Errore durante la query al database");
    sqlite3_free(error_message);
    sqlite3_close(database);
    exit(EXIT_FAILURE);
  }

  sqlite3_close(database);

  return 0;
}

int callback(void *socketPtr, int argc, char **argv, char **colName){
  int socket = *((int *) socketPtr);  /*socket to send data to client*/
  char dataRetrieved[MAX_DATA_RETRIEVED];
  char *tmp;

  sprintf(dataRetrieved, "%s;", argv[0]);
  send(socket, dataRetrieved, 50, 0);

  double latToSend = strtod(argv[1], &tmp); /*from string to double because argv is char** */
  sprintf(dataRetrieved, "%f;", latToSend);
  send(socket, dataRetrieved, 50, 0);

  double lonToSend = strtod(argv[2], &tmp); /*from string to double because argv in char** */
  sprintf(dataRetrieved, "%f\n", lonToSend);
  send(socket, dataRetrieved, 50, 0);

  printf("\n");
  return 0;
}

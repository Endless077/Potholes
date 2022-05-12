#include <stdio.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sqlite3.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <string.h>

#include "./sql.h"

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
  int status_query;
  sqlite3_stmt *res;
  char dataRetrieved[MAX_DATA_RETRIEVED]; /*buffer*/

  get_query = sqlite3_mprintf("SELECT * FROM Potholes WHERE latitude >= %f AND latitude <= %f AND longitude >= %f AND longitude <= %f;", latitudine-distanza, latitudine+distanza, longitudine-distanza, longitudine+distanza);
  /*PROVARE A RIFARE I PARAMETRI CON sqlite3_bind*/

  if(get_query == NULL){
    perror("Errore durante la costruzione della query\n");
    exit(EXIT_FAILURE);
  }

  status_query = sqlite3_prepare_v2(database, get_query, strlen(get_query), &res, NULL);

  if(status_query != SQLITE_OK){
    perror("Errore durante la  query\n");
    exit(EXIT_FAILURE);
  }


  while(sqlite3_step(res) == SQLITE_ROW){
    /*cleaning the buffer*/
    bzero((char*) &dataRetrieved, sizeof(dataRetrieved));

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 0));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 1));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 2));
    strcat(dataRetrieved, ":");

    send(socket, dataRetrieved, sizeof(dataRetrieved), 0);

    printf("Inviati i dati al client \n");

  }

  send(socket, "end", 3, 0);

  sqlite3_finalize(res);

  return 0;
}

int getAllPotholes(sqlite3 *database, int socket){
  int status_query;
  char *get_query = "SELECT * FROM Potholes";
  char dataRetrieved[MAX_DATA_RETRIEVED];
  sqlite3_stmt *res;

  status_query = sqlite3_prepare_v2(database, get_query, strlen(get_query), &res, NULL);

  if(status_query != SQLITE_OK){
    perror("Errore durante la  query\n");
    exit(EXIT_FAILURE);
  }

  while(sqlite3_step(res) == SQLITE_ROW){
    /*cleaning the buffer*/
    bzero((char*) &dataRetrieved, sizeof(dataRetrieved));

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 0));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 1));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 2));
    strcat(dataRetrieved, ":");

    send(socket, dataRetrieved, sizeof(dataRetrieved), 0);

    printf("Inviati i dati al client \n");
    /*controllare se si può fare tutto in un'unica sprintf e send*/
  }

  send(socket, "end", 3, 0);

  sqlite3_close(database);

  return 0;
}

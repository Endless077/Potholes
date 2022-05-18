/*
_____  _____  _________  _____  _____      ______
|_   _||_   _||  _   _  ||_   _||_   _|   .' ____ \
  | |    | |  |_/ | | \_|  | |    | |     | (___ \_|
  | '    ' |      | |      | |    | |   _  _.____`.
   \ \__/ /      _| |_    _| |_  _| |__/ || \____) |
    `.__.'      |_____|  |_____||________| \______.'
*/

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <sqlite3.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#include "./utils.h"

#define MAX_DATA_RETRIEVED 100

/*Insert new Potholes*/
int insertNewPothole(sqlite3* database, char *username, double latitudine, double longitudine) {
    int status_query;
    char *put_query;
    char *tag = "SQL_Insert";

    /*Setting query string (%q for using ')*/
    put_query = sqlite3_mprintf("INSERT INTO Potholes VALUES ('%q', %f, %f);", username, latitudine, longitudine);

    if(put_query == NULL)
      logging(tag, "Costruzione della query fallita", false);

    /*Execute query string*/
    status_query = sqlite3_exec(database, put_query, NULL, NULL, NULL);

    if(status_query != SQLITE_OK)
      logging(tag, "Errore durante la query", false);

    return status_query;
}

/*Getting Near Potholes*/
int getNearPotholes(sqlite3 *database, int socket, double latitudine, double longitudine, double distanza) {
  int status_query;
  char *get_query;
  char *tag = "SQL_NearPotholes";

  sqlite3_stmt *res;
  char dataRetrieved[MAX_DATA_RETRIEVED]; /*buffer*/

  /*Setting query string*/
  get_query = sqlite3_mprintf("SELECT * FROM Potholes WHERE latitude >= %f AND latitude <= %f AND longitude >= %f AND longitude <= %f;", latitudine-distanza, latitudine+distanza, longitudine-distanza, longitudine+distanza);

   if(get_query == NULL)
      logging(tag, "Costruzione della query fallita", false);

  /*Prepare query (return status)*/
  status_query = sqlite3_prepare_v2(database, get_query, strlen(get_query), &res, NULL);

  if(status_query != SQLITE_OK)
      logging(tag, "Errore durante la query", false);

  /*Row Cursor*/
  while(sqlite3_step(res) == SQLITE_ROW) {

    /*Cleaning the buffer*/
    bzero((char*) &dataRetrieved, sizeof(dataRetrieved));

    /*Setting format string username:latitude:longitude:*/
    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 0));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 1));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 2));
    strcat(dataRetrieved, ":\r");

    /*Sending data*/
    send(socket, dataRetrieved, sizeof(dataRetrieved), 0);

    char msg[250];
    sprintf(msg, "Sending row: %s", dataRetrieved);
    logging(tag, msg, true);
  }

  /*End char* flagging*/
  send(socket, "END", 3, 0);
  sqlite3_finalize(res);

  return 0;
}

/*Getting All Potholes*/
int getAllPotholes(sqlite3 *database, int socket) {
  int status_query;
  char *get_query = "SELECT * FROM Potholes";
  char *tag = "SQL_GetAll";
  char dataRetrieved[MAX_DATA_RETRIEVED];

  sqlite3_stmt *res;

  /*Prepare query (return status)*/
  status_query = sqlite3_prepare_v2(database, get_query, strlen(get_query), &res, NULL);

  if(status_query != SQLITE_OK)
    logging(tag, "Errore durante la query", false);

  /*Row Cursor*/
  while(sqlite3_step(res) == SQLITE_ROW) {

    /*Cleaning the buffer*/
    bzero((char*) &dataRetrieved, sizeof(dataRetrieved));

    /*Setting format string username:latitude:longitude:*/
    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 0));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 1));
    strcat(dataRetrieved, ":");

    strcat(dataRetrieved, (char *)sqlite3_column_text(res, 2));
    strcat(dataRetrieved, ":\r");

    /*Sending data*/
    send(socket, dataRetrieved, sizeof(dataRetrieved), 0);

    char msg[250];
    sprintf(msg, "Sending row: %s", dataRetrieved);
    logging(tag, msg, true);
  }

  /*End char* flagging*/
  send(socket, "END", 3, 0);
  sqlite3_close(database);

  return 0;
}

/*Support logging function*/
void logging(char* tag, char* messaggio, bool status) {
  char format[250];
  bzero((char*) &format, sizeof(format));

  time_t now;
  time(&now);

  sprintf(format, "[%.24s] %s: %s\n", ctime(&now), tag, messaggio);

  if(status)
    printf("%s", format);
  else{
    perror(format);
    exit(EXIT_FAILURE);
  }
}

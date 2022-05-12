#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include<sys/socket.h>
#include <string.h>
#include <sqlite3.h>

#include "./sql.c"

#include "./startserver.h"
//#include dbpath


sqlite3* database;

int main(void) {

  char* dbname = "potholes.db";
  int status;

  status = sqlite3_open_v2(dbname, &database, SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE| SQLITE_OPEN_NOMUTEX, NULL);

  if(status != SQLITE_OK) {
    perror("errore durante l'apertura del database\n");
    exit(EXIT_FAILURE);
  }else{
    printf("Avvio Database avvenuto con successo\n");
  }

    createInitialTable();

    insertInitialValues();

    printf("Inserimento avvenuto con successo\n");

    printf("FINE CREAZIONE DATABASE\n");
    return 0;

}

void createInitialTable(){
  char* createQuery;
  int status_query;

  createQuery = "CREATE TABLE IF NOT EXISTS Potholes (username VARCHAR(20), latitude DOUBLE, longitude DOUBLE);";
  status_query = sqlite3_exec(database, createQuery, NULL, NULL, NULL);

  if(status_query != SQLITE_OK){
    perror("Errore durante la creazione della tabella\n");
    exit(EXIT_FAILURE);
  }else
    printf("Creazione della tabella avvenuta con successo\n");
}


void insertInitialValues(){
  char* insertQuery = "INSERT INTO Potholes VALUES('Valentino','40.835884', '14.248767')";
  int status_query = sqlite3_exec(database, insertQuery, NULL, NULL, NULL);

  if(status_query != SQLITE_OK){
    perror("Errore durante l'inserimento dei primi valore nel database\n");
    exit(EXIT_FAILURE);
  }

  insertQuery = "INSERT INTO Potholes VALUES('Lucia', '40.835888', '14248769')";
  status_query = sqlite3_exec(database, insertQuery, NULL, NULL, NULL);

  if(status_query != SQLITE_OK){
    perror("Errore durante l'inserimento dei primi valori nel database\n");
    exit(EXIT_FAILURE);
  }

}

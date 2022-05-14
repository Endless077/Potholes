/*
  ______   _________     _       _______   _________  ________  _______
.' ____ \ |  _   _  |   / \     |_   __ \ |  _   _  ||_   __  ||_   __ \
| (___ \_||_/ | | \_|  / _ \      | |__) ||_/ | | \_|  | |_ \_|  | |__) |
 _.____`.     | |     / ___ \     |  __ /     | |      |  _| _   |  __ /
| \____) |   _| |_  _/ /   \ \_  _| |  \ \_  _| |_    _| |__/ | _| |  \ \_
 \______.'  |_____||____| |____||____| |___||_____|  |________||____| |___|
*/

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <sqlite3.h>
#include <unistd.h>

#include "./utils.h"
#include "./startserver.h"

sqlite3* database;

int main(void) {

  int status;
  char* dbname = "potholes.db";
  char* tag = "Load Database";

  status = sqlite3_open_v2(dbname, &database, SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE| SQLITE_OPEN_NOMUTEX, NULL);

  if(status != SQLITE_OK)
    logging(tag, "Error starting Database", false);
  else
    logging(tag, "Success starting database", true);

  createInitialTable();

  insertInitialValues();

  logging(tag, "Database complete", true);
  return 0;
}

/*Create database*/
void createInitialTable() {
  int status_query;
  char* createQuery;
  char* tag = "Create Table";

  createQuery = "CREATE TABLE IF NOT EXISTS Potholes (username VARCHAR(20), latitude DOUBLE, longitude DOUBLE);";
  status_query = sqlite3_exec(database, createQuery, NULL, NULL, NULL);

  if(status_query != SQLITE_OK)
    logging(tag, "Table creation error", false);
  else
    logging(tag, "Table creation success", true);
}

/*Initialize table*/
void insertInitialValues() {
  int status_query;
  char* tag = "Insert Table";

  char* insertQuery = "INSERT INTO Potholes VALUES('Valentino','40.835884', '14.248767')";
  status_query = sqlite3_exec(database, insertQuery, NULL, NULL, NULL);

  if(status_query != SQLITE_OK)
    logging(tag, "Insert error", false);

  insertQuery = "INSERT INTO Potholes VALUES('Lucia', '40.835777', '14.248769')";
  status_query = sqlite3_exec(database, insertQuery, NULL, NULL, NULL);

  if(status_query != SQLITE_OK)
    logging(tag, "Insert error", false);

  insertQuery = "INSERT INTO Potholes VALUES('Mariano', '40.123456', '14.123456')";
  status_query = sqlite3_exec(database, insertQuery, NULL, NULL, NULL);

  if(status_query != SQLITE_OK)
    logging(tag, "Insert error", false);

  logging(tag, "Insert success", true);
}

//Headers of the utils.c

#ifndef Utils
#define Utils
int insertNewPothole(sqlite3* database, char *username, double latitudine, double longitudine);                     // insert the pothole with arguments as parameters
int getNearPotholes(sqlite3* database, int socket, double latitudine, double longitudine, double distanza);         // get Potholes close to position latitudine and longitudine
int getAllPotholes(sqlite3* database, int socket);                                                                  // get all Potholes
void logging(char* tag, char* messaggio, bool status);                                                              // logging function
#endif

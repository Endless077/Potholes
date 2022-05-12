//Headers of the sql.c
#ifndef isDefined
#define isDefined
int getNearPotholes(sqlite3* database, int socket, double latitudine, double longitudine, double distanza);        // get Potholes close to position latitudine and longitudine
int getAllPotholes(sqlite3* database, int socket);                                                                 // get all Potholes
int insertNewPothole(sqlite3* database, char *username, double latitudine, double longitudine);                    // Insert the pothole with arguments as parameters
#endif

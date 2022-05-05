//Headers of the sql.c

int getNearPotholes(sqlite3* database, int socket, double latitudine, double longitudine, double distanza);        // get Potholes close to position latitudine and longitudine
int getAllPotholes(sqlite3* database, int socket);                                                                 // get all Potholes
int insertNewPothole(sqlite3* database, char *username, double latitudine, double longitudine);                    // Insert the pothole with arguments as parameters
int callback(void *, int, char **, char **);                                                                       // callback function called by sqlite3 for each result

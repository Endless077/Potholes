//Headers of the sql.c

int callback(void *, int, char **, char **);                                                                       // Callback function called by sqlite3 for each result

int getNearPotholes(sqlite3* database, int socket, double latitudine, double longitudine, double distanza);        // Get Potholes close to position latitudine and longitudine
int getAllPotholes(sqlite3* database, int socket);                                                                 // Get all Potholes
int insertNewPothole(sqlite3* database, char *username, double latitudine, double longitudine);                    // POST a new pothole with arguments as parameters

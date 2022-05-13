//Headers of the server.c

#ifndef Server
#define Server
void* manageRequest(void* socketDescriptor);
void getAllPotholesRequest(int socketDescriptor, sqlite3 *);
void getNearPotholesRequest(int socketDescriptor, sqlite3 *);
void postRequest(int socketDescriptor, sqlite3 *);
void signalHandler(int signal);
#endif

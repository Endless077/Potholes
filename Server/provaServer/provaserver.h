//H server.h

void* manageRequest(void* socketDescriptor);
void getAllPotholesRequest(int socketDescriptor, sqlite3 *);
void getNearPotholesRequest(int socketDescriptor, sqlite3 *);
void postRequest(int socketDescriptor, sqlite3 *);
void signUSRHandler(int signal);

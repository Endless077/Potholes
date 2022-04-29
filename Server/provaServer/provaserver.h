//provaserver.h

void* threadFunction(void* socketDescriptor);
void getRequest(int socketDescriptor);
void postRequest(int socketDescriptor);

void signUSRHandlet(int signal);

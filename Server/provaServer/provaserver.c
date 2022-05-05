#include <stdio.h>
#include <netdb.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>

#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <signal.h>

#include "./sql.c"
#include "./provaserver.h"

#define MAX 80
#define MAX_GET_BUFFER 1000
#define PORT 8080
#define SA struct sockaddr

void signalHandler(int signal);
void *manageRequest(void *SocketDescriptorClient);

static int sockfd;

int main(void) {

  int  connfd, len;
  int *personal_socket;
  struct sockaddr_in serveraddr, clientaddr;
  char *hostname = "0.0.0.0";

  /*declaring handler for SIGUSR1 and SIGUSR2*/
  signal(SIGUSR1, signalHandler);
  signal(SIGUSR2, signalHandler);

  /*create socket*/
  sockfd = socket(AF_INET, SOCK_STREAM, 0);
  if(sockfd == -1){
    perror("error socket server");
    exit(1);
  }else
    printf("socket created\n");

  /*assigning IP, Port and sin_family */
  serveraddr.sin_family = AF_INET;
  serveraddr.sin_addr.s_addr = inet_addr(hostname);
  serveraddr.sin_port = htons(PORT);

  /*binding*/
  if(bind(sockfd, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) != 0){
    perror("error binding");
    exit(1);
  }else
    printf("binding success\n");

  if(listen(sockfd, 10) != 0){
    perror("error listen server");
    exit(1);
  }

  /*accepting requests from the clients on the sockfd and return the new socket descriptor to connfd*/
  while(connfd = accept(sockfd, (struct sockaddr *) &clientaddr, &len) > 0) {
    printf("Connection estabilished");

    personal_socket = malloc(sizeof(int));
    *(personal_socket) = connfd;
    pthread_t thread;

    int status_create_thread;
    status_create_thread = pthread_create(&thread, NULL, manageRequest, (void *) personal_socket);

    if(status_create_thread < 0) {
      perror("Errore durante la creazione di un thread\n");
      exit(EXIT_FAILURE);
    }

  }

  perror("error accept, shutdown server \n");
  close(sockfd);
  return 0;

}

/*Handler of signal usr*/
void signalHandler(int signal) {
  printf("Ricevuto il segnale SIGUSR (codice: %d). Close della socket ed il server sara' offline\n", signal);
  close(sockfd);
  printf("server spento\n");
  exit(EXIT_SUCCESS);
}

void *manageRequest(void *SocketDescriptorClient) {
  printf("thread avviato\n");
  int status;
  sqlite3 *database;

  char buffer[50];
  int socket_descriptor = *((int *) SocketDescriptorClient);

  printf("gestisco la richiesta del client\n");

  status = sqlite3_open_v2("potholes.db", &database, SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE | SQLITE_OPEN_NOMUTEX, NULL);

  if(status != SQLITE_OK)
    perror("Errore durante l'apertura del database\n");
  else{

    recv(socket_descriptor, buffer, 50, 0);

    if(strcmp(buffer, "getAll") == 0)
        getAllPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "getNear") == 0)
        getNearPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "post") == 0)
        postRequest(socket_descriptor, database);
    else if(strcmp(buffer, "threshold") == 0)
        send(socket_descriptor, "0.000003:", 10, 0);


    printf("Richiesta del client servita, chiudo la connessione col client\n");

  }
  sqlite3_close(database);
  close(socket_descriptor);
}

void  getAllPotholesRequest(int socket, sqlite3 *database){
  int status;

  status = getAllPotholes(database, socket);
  if(status != SQLITE_OK)
      perror("Errore durante la query al database\n");
  else
      printf("Query avvenuta con successo\n");

}

void getNearPotholesRequest(int socket, sqlite3 *database){
  int status;
  char buffer[MAX_GET_BUFFER];
  char tmp[50];
  double lat, lon, distanza;

  //NO USERNAME HERE

  recv(socket, buffer, 1000, 0);

  char *actualParam = strtok(buffer, ":");

  if(actualParam == NULL)
    perror("Errore durante la ricezione dei dati da parte del client\n");
  else{
    strcpy(tmp, actualParam);
    lat = atof(tmp);
    bzero((char*) &tmp, sizeof(tmp));

    actualParam = strtok(NULL, ":");

    strcpy(tmp, actualParam);
    lon = atof(tmp);
    bzero((char*) &tmp, sizeof(tmp));

    actualParam = strtok(NULL, ":");

    strcpy(tmp, actualParam);
    distanza = atof(tmp);
    bzero((char*) &tmp, sizeof(tmp));
  }

  printf("Dati recuperati dal client: latitudine %f longitudine %f \n", lat, lon);

  status = getNearPotholes(database, socket, lat, lon, distanza);

  if(status != SQLITE_OK)
    perror("Errore durante la query al database\n");
  else
    printf("Query avvenuta con successo\n");

}

void postRequest(int socket, sqlite3 *database){
  int status;
  char buffer[MAX_GET_BUFFER];
  char tmp[50];
  char username[50];
  double lat, lon;

  recv(socket, buffer, 1000, 0);

  char *actualParam = strtok(buffer, ":");

  if(actualParam == NULL)
    perror("Errore durante la ricezione dei dati da parte del client\n");
  else{
    strcpy(username, actualParam);

    actualParam = strtok(NULL, ":");

    strcpy(tmp, actualParam);
    lat = atof(tmp);
    bzero((char *) &tmp, sizeof(tmp));

    actualParam = strtok(NULL, ":");

    strcpy(tmp, actualParam);
    lon = atof(tmp);
    bzero((char *) &tmp, sizeof(tmp));
  }

  printf("Dati recuperati dal client: username %s latitudine %f longitudine %f\n", username, lat, lon);

  status = insertNewPothole(database, username, lat, lon);

  if(status != SQLITE_OK)
    perror("Errore durante l'inserimento dei dati della nuova buca rilevata\n");
  else
    printf("Successo nell'inserimento dei dati della nuova buca rilevata\n");
}

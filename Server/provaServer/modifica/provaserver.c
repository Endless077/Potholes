#include <stdio.h>
#include <netdb.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>

#include <time.h>

#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <signal.h>

#include <sqlite3.h>

#include "./sql.h"
#include "./provaserver.h"

#define MAX 80
#define MAX_GET_BUFFER 1000
#define PORT 5678
#define SA struct sockaddr

#define SOCKET_NAME "my_server"
#include<sys/un.h>

// void signalHandler(int signal);
// void *manageRequest(void *SocketDescriptorClient);

static int sockfd;

int main(void) {

  int  connfd, len;
  int *personal_socket;
  struct sockaddr_in serveraddr, clientaddr;
  // struct sockaddr_un serveraddr, clientaddr;

  char *hostname = "0.0.0.0";

  /*declaring handler for SIGUSR1 and SIGUSR2*/
  signal(SIGUSR1, signalHandler);
  signal(SIGUSR2, signalHandler);

  /*create socket*/

  sockfd = socket(AF_INET, SOCK_STREAM, 0);
  // sockfd = socket(PF_UNIX, SOCK_STREAM, 0);

  if(sockfd == -1){
    perror("error socket server");
    exit(1);
  }else
    printf("socket created\n");

  bzero(&serveraddr, sizeof(serveraddr));

  /*Assigning IP, Port and sin_family */

  serveraddr.sin_family = AF_INET;
  serveraddr.sin_addr.s_addr = inet_addr(hostname);
  serveraddr.sin_port = htons(PORT);

  // serveraddr.sun_family = AF_LOCAL;
  // strcpy(serveraddr.sun_path, SOCKET_NAME);

  /*binding*/

  if(bind(sockfd, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) == -1){
    perror("error binding");
    exit(1);
  }else
    printf("binding success\n");

  if(listen(sockfd, 10) == -1){
    perror("error listen server");
    exit(1);
  }

  len = sizeof(clientaddr);
  while((connfd = accept(sockfd, (struct sockaddr *) &clientaddr, (socklen_t*)&len)) != -1) {
    /*Accepting requests from the clients on the sockfd and return the new socket descriptor to connfd*/
    printf("Connection estabilished\n");

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
  int socket_descriptor = *(int *) SocketDescriptorClient;

  printf("gestisco la richiesta del client socket %d\n", socket_descriptor);

  status = sqlite3_open_v2("potholes.db", &database, SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE | SQLITE_OPEN_NOMUTEX, NULL);

  if(status != SQLITE_OK)
    perror("Errore durante l'apertura del database\n");
  else{

    // bzero(buffer, 50);
    // recv(socket_descriptor, buffer, 50, 0);
    read(socket_descriptor, buffer, sizeof(buffer));
    // bzero(buffer, 50);
    // read(socket_descriptor, buffer, sizeof(buffer));

    printf("buffer: %s\n", buffer);

    if(strcmp(buffer, "getAll") == 0)
      getAllPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "getNear") == 0)
      getNearPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "post") == 0)
      postRequest(socket_descriptor, database);
    else if(strcmp(buffer, "threshold") == 0){
      send(socket_descriptor, "0.000003;", 10, 0);
      printf("entrato nell'if\n");
      }


    printf("Richiesta del client servita, chiudo la connessione col client\n");

  }
  sqlite3_close(database);
  close(socket_descriptor);

  return NULL;
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
  send(socket, "ok", 2, 0);

  recv(socket, buffer, MAX_GET_BUFFER, 0);

  printf("buffer all'interno di getNearPotholesRequest %s\n", buffer);

  char *actualParam = strtok(buffer, ":");

  if(actualParam == NULL)
    perror("Errore durante la ricezione dei dati da parte del client\n");
  else{
    actualParam = strtok(NULL, ":");
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

  send(socket, "ok", 2, 0);

  recv(socket, buffer, MAX_GET_BUFFER, 0);

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

  if(status != SQLITE_OK){
    perror("Errore durante l'inserimento dei dati della nuova buca rilevata\n");
    send(socket, "error", 5, 0);
  }
  else{
    printf("Successo nell'inserimento dei dati della nuova buca rilevata\n");
    send(socket, "success", 7, 0);    
  }
}

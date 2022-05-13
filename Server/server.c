#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <signal.h>
#include <sqlite3.h>
#include <netdb.h>
#include <netinet/in.h>
#include <unistd.h>

#include "./Other/utils.h"
#include "./server.h"

#define MAX_GET_BUFFER 1000
#define PORT 5678

static int sockfd;

int main(void) {

  int  connfd, len;
  int *personal_socket;
  struct sockaddr_in serveraddr, clientaddr;

  char *hostname = "0.0.0.0";
  char *tag = "Main Thread";

  /*Handler declare for SIGUSR1 and SIGUSR2*/
  signal(SIGUSR1, signalHandler);
  signal(SIGUSR2, signalHandler);

  /*Create socket*/
  sockfd = socket(AF_INET, SOCK_STREAM, 0);

  if(sockfd == -1)
    logging(tag, "Socket creation error", false);
  else
    logging(tag, "Socket creation success", true);

  bzero(&serveraddr, sizeof(serveraddr));

  /*Assigning IP, Port and sin_family */
  serveraddr.sin_family = AF_INET;
  serveraddr.sin_addr.s_addr = inet_addr(hostname);
  serveraddr.sin_port = htons(PORT);

  /*Binding*/
  if(bind(sockfd, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) == -1)
    logging(tag, "Socket bind error", false);
  else
    logging(tag, "Socket bind success", true);

  if(listen(sockfd, 10) == -1)
    logging(tag, "Listen status: online", true);

  len = sizeof(clientaddr);

  while((connfd = accept(sockfd, (struct sockaddr *) &clientaddr, (socklen_t*)&len)) != -1) {
    
    /*Accepting requests from the clients on the sockfd and return the new socket descriptor to connfd*/
    logging(tag, "Accept success", true);

    personal_socket = malloc(sizeof(int));
    *(personal_socket) = connfd;
    pthread_t thread;

    int status_create_thread;
    status_create_thread = pthread_create(&thread, NULL, manageRequest, (void *) personal_socket);

    if(status_create_thread < 0)
      logging(tag, "Pthread creation error", false);

  }

  logging(tag, "Accept error", false);
  close(sockfd);
  return 0;

}

/*Handler of signal usr*/
void signalHandler(int signal) {
  char *tag = "Handler";
  char sig[10];
  sprintf(sig,"%d",signal);

  char *msg = strcat("Ricevuto il segnale SIGUSR (codice: ", sig);
  msg = strcat(msg, "). Close della socket ed il server sara' offline\n"")");
  logging(tag, msg, true);

  close(sockfd);
  
  logging(tag, "Server spento", true);
  exit(EXIT_SUCCESS);
}

/*Manage Request Thread Function*/
void *manageRequest(void *SocketDescriptorClient) {
  int status;
  sqlite3 *database;
  char buffer[50];
  char *tag = "Request manager";
  int socket_descriptor = *(int *) SocketDescriptorClient;

  logging(tag, "Thread request started", true);

  /*Connect to database*/
  status = sqlite3_open_v2("potholes.db", &database, SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE | SQLITE_OPEN_NOMUTEX, NULL);

  if(status != SQLITE_OK)
    logging(tag, "Database opening error", false);
  else{

    /*Cleaning buffer*/
    bzero(buffer, sizeof(buffer));

    /*Reading request*/
    read(socket_descriptor, buffer, sizeof(buffer));

    logging(tag, strcat("Request from client: ", buffer), true);

    /*Service selector*/
    if(strcmp(buffer, "getAll") == 0)
      getAllPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "getNear") == 0)
      getNearPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "post") == 0)
      postRequest(socket_descriptor, database);
    else if(strcmp(buffer, "threshold") == 0)
      send(socket_descriptor, "0.000003:", 10, 0);
    else {
      logging(tag, "Invalid service", false);
      send(socket_descriptor, "Invalid service:", 17, 0);
    }

    logging(tag, "Service provided", true);
  }

  /*Close socket and database*/
  sqlite3_close(database);
  close(socket_descriptor);

  return NULL;
}

/*Get All Potholes Request*/
void  getAllPotholesRequest(int socket, sqlite3 *database){
  int status;
  char *tag = "Get All Request";
  status = getAllPotholes(database, socket);
  
  if(status != SQLITE_OK)
    logging(tag, "Query not complete", false);
  else
    logging(tag, "Query complete", true);
}

/*Get Near Potholes Request*/
void getNearPotholesRequest(int socket, sqlite3 *database){
  int status;
  char buffer[MAX_GET_BUFFER];
  char *tag = "Get Near request";
  
  char tmp[50];
  char username[50];
  double lat, lon, distanza;

 /*Init conversation*/
  send(socket, "Start", 7, 0);
  recv(socket, buffer, sizeof(buffer), 0);

  char *actualParam = strtok(buffer, ":");

  if(actualParam == NULL)
    logging(tag, "Recive data: error", false);
  else{

    /*Getting data from client*/
    logging(tag, strcat("Recive data: ", buffer), true);
    strcpy(username, actualParam);
    
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

  char *msg = "";
  sprintf(msg,"Data from %s at %f - %f with max range %f", username, lat, lon, distanza);
  logging(tag, msg, true);

  status = getNearPotholes(database, socket, lat, lon, distanza);

   if(status != SQLITE_OK)
    logging(tag, "Query not complete", false);
  else
    logging(tag, "Query complete", true);

}

/*Post New Potholes Request*/
void postRequest(int socket, sqlite3 *database){
  int status;
  char buffer[MAX_GET_BUFFER];
  char *tag = "Post request";
  
  char tmp[50];
  char username[50];
  double lat, lon;

  /*Init conversation*/
  send(socket, "Start", 7, 0);
  recv(socket, buffer, MAX_GET_BUFFER, 0);

  char *actualParam = strtok(buffer, ":");

  if(actualParam == NULL)
    logging(tag, "Recive data: error", false);
  else{

    /*Getting data from client*/
    logging(tag, strcat("Recive data: ", buffer), true);
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

  char *msg = "";
  sprintf(msg,"Data from %s at %f - %f", username, lat, lon);
  logging(tag, msg, true);

  logging(tag, msg, true);

  /*Call insert function*/
  status = insertNewPothole(database, username, lat, lon);

  //Status check
  if(status != SQLITE_OK) {
    logging(tag, "Query uncomplete", false);
    send(socket, "error", 5, 0);
  }else{
    logging(tag, "Query complete", true);
    send(socket, "success", 7, 0);
  }
}

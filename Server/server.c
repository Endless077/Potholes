/*
  ______   ________  _______  ____   ____  ________  _______
.' ____ \ |_   __  ||_   __ \|_  _| |_  _||_   __  ||_   __ \
| (___ \_|  | |_ \_|  | |__) | \ \   / /    | |_ \_|  | |__) |
 _.____`.   |  _| _   |  __ /   \ \ / /     |  _| _   |  __ /
| \____) | _| |__/ | _| |  \ \_  \ ' /     _| |__/ | _| |  \ \_
 \______.'|________||____| |___|  \_/     |________||____| |___|
*/

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

#define MAX_MSG_SIZE 250
#define MAX_BUFFER_SIZE 100
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
  signal(SIGINT, signalHandler);

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

  /*Attribute Thread*/
  int err;
  pthread_attr_t attr;

  /*Status*/
  int status_create_thread;
  pthread_t thread;

  /*Accepting requests from the clients on the sockfd and return the new socket descriptor to connfd*/
  while((connfd = accept(sockfd, (struct sockaddr *) &clientaddr, (socklen_t*)&len)) != -1) {
    
    /*Initi attribute*/
    err = pthread_attr_init(&attr);
    
    if(err!=0)
      logging(tag, "Attribute init error", false);
  
    /*Set detach state*/
    err = pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
  
    if(err!=0)
      logging(tag, "Setting detach state error", false);

    logging(tag, "Accept success", true);
    
    personal_socket = malloc(sizeof(int));
    *(personal_socket) = connfd;

    /*IP logging*/
    char ip[25];
    if(inet_ntop(AF_INET, &clientaddr.sin_addr, ip, sizeof(ip))) {
      char msg[100];
      sprintf(msg, "Client connect from: %s", ip);
      logging(tag, msg, true);
    }else
      logging(tag, "Invalid Client IP", false);

    /*Start a new manage request*/
    status_create_thread = pthread_create(&thread, &attr, manageRequest, (void *) personal_socket);

    if(status_create_thread < 0)
      logging(tag, "Pthread creation error", false);

    pthread_attr_destroy(&attr);
  

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

  char msg[MAX_MSG_SIZE];
  sprintf(msg,"Ricevuto il segnale (codice: %s)", sig);
  logging(tag, msg, true);

  close(sockfd);

  logging(tag, "Server spento", true);
  exit(EXIT_SUCCESS);
}

/*Manage Request Thread Function*/
void *manageRequest(void *clientSocket) {
  int status;
  sqlite3 *database;

  char msg[MAX_MSG_SIZE];
  char buffer[MAX_BUFFER_SIZE];
  char *tag = "Request manager";

  int socket_descriptor = *(int *) clientSocket;

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

    sprintf(msg, "Request from client: %s", buffer);
    logging(tag, msg, true);
    bzero((char*) &msg, sizeof(msg));

    /*Service selector*/
    if(strcmp(buffer, "getAll") == 0)
      getAllPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "getNear") == 0)
      getNearPotholesRequest(socket_descriptor, database);
    else if(strcmp(buffer, "put") == 0)
      postRequest(socket_descriptor, database);
    else if(strcmp(buffer, "threshold") == 0)
      send(socket_descriptor, "0.000003\r", 10, 0);
    else {
      logging(tag, "Invalid service", false);
      send(socket_descriptor, "Invalid service:", 17, 0);
    }

    logging(tag, "Service provided", true);
  }

  /*Close socket and database*/
  sqlite3_close(database);
  free(clientSocket);
  close(socket_descriptor);

  return NULL;
}

/*Get All Potholes Request*/
void  getAllPotholesRequest(int socket, sqlite3 *database) {
  int status;
  char *tag = "Get All Request";
  status = getAllPotholes(database, socket);

  if(status != SQLITE_OK)
    logging(tag, "Query not complete", false);
  else
    logging(tag, "Query complete", true);
}

/*Get Near Potholes Request*/
void getNearPotholesRequest(int socket, sqlite3 *database) {
  int status;
  char *tag = "Get Near request";

  char utils[MAX_MSG_SIZE];
  char buffer[MAX_BUFFER_SIZE];

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
    sprintf(utils, "Request from client: %s", buffer);
    logging(tag, utils, true);
    bzero((char*) &utils, sizeof(utils));

    strcpy(username, actualParam);

    actualParam = strtok(NULL, ":");

    strcpy(utils, actualParam);
    lat = atof(utils);
    bzero((char*) &utils, sizeof(utils));

    actualParam = strtok(NULL, ":");

    strcpy(utils, actualParam);
    lon = atof(utils);
    bzero((char*) &utils, sizeof(utils));

    actualParam = strtok(NULL, ":");

    strcpy(utils, actualParam);
    distanza = atof(utils);
    bzero((char*) &utils, sizeof(utils));
  }

  /*Operation logging*/
  sprintf(utils,"Data from %s at %f - %f with max range %f", username, lat, lon, distanza);
  logging(tag, utils, true);
  bzero((char*) &utils, sizeof(utils));

  status = getNearPotholes(database, socket, lat, lon, distanza);

   if(status != SQLITE_OK)
    logging(tag, "Query not complete", false);
  else
    logging(tag, "Query complete", true);

}

/*Post New Potholes Request*/
void postRequest(int socket, sqlite3 *database){
  int status;
  char *tag = "Post request";

  char utils[MAX_MSG_SIZE];
  char buffer[MAX_BUFFER_SIZE];

  char username[50];
  double lat, lon;

  /*Init conversation*/
  send(socket, "Start", 7, 0);
  recv(socket, buffer, MAX_BUFFER_SIZE, 0);

  char *actualParam = strtok(buffer, ":");

  if(actualParam == NULL)
    logging(tag, "Recive data: error", false);
  else{

    /*Getting data from client*/
    sprintf(utils, "Request from client: %s", buffer);
    logging(tag, utils, true);
    bzero((char*) &utils, sizeof(utils));

    strcpy(username, actualParam);

    actualParam = strtok(NULL, ":");

    strcpy(utils, actualParam);
    lat = atof(utils);
    bzero((char *) &utils, sizeof(utils));

    actualParam = strtok(NULL, ":");

    strcpy(utils, actualParam);
    lon = atof(utils);
    bzero((char *) &utils, sizeof(utils));
  }

  /*Operation logging*/
  sprintf(utils,"Data from %s at %f - %f", username, lat, lon);
  logging(tag, utils, true);
  bzero((char *) &utils, sizeof(utils));

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

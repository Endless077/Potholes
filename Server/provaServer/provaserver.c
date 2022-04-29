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

#define MAX 80
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

  /*Assigning IP, Port and sin_family */

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

  /*Accepting requests from the clients on the sockfd and return the new socket descriptor to connfd*/
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

  int socket_descriptor = *((int *) SocketDescriptorClient);
  char buffer[50];

}

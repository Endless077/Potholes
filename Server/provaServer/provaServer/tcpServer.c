#include <stdio.h>
#include <fcntl.h> // for open
#include <unistd.h> // for close
#include <netdb.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>

#include<sys/un.h>

#include <pthread.h>


#define MAX 80
#define PORT 8080
#define SA struct sockaddr
#define SOCKET_NAME "my_server"

void *manageRequest(void *SocketDescriptorClient);


// Function designed for chat between client and server.
void func(int connfd)
{
	char buff[MAX];
	    int n;
	    // infinite loop for chat

	        // read the message from client and copy it in buffer
	        read(connfd, buff, 4);
	        // print buffer which contains the client contents
	        printf("From client: %s\t To client : \n", buff);

	        // and send that buffer to client
	        write(connfd, buff, 4);




}

// Driver function
int main()
{
	int sockfd, connfd, len;
	struct sockaddr_un servaddr, cli;
	int *personal_socket;

	// socket create and verification
	sockfd = socket(PF_UNIX, SOCK_STREAM, 0);
	if (sockfd == -1) {
		printf("socket creation failed...\n");
		exit(0);
	}
	else
		printf("Socket successfully created..\n");
	bzero(&servaddr, sizeof(servaddr));

	// assign IP, PORT
	// servaddr.sin_family = AF_INET;
	// servaddr.sin_addr.s_addr = SOCKET_NAME;
	// servaddr.sin_port = htons(PORT);
  servaddr.sun_family = AF_LOCAL;
  strcpy(servaddr.sun_path, SOCKET_NAME);



	// Binding newly created socket to given IP and verification
  if(bind(sockfd, (struct sockaddr *) &servaddr, sizeof(servaddr)) != 0){
		printf("socket bind failed...\n");
		exit(0);
	}
	else
		printf("Socket successfully binded..\n");

	// Now server is ready to listen and verification
	if ((listen(sockfd, 5)) != 0) {
		printf("Listen failed...\n");
		exit(0);
	}
	else
		printf("Server listening..\n");
	len = sizeof(cli);

	// Accept the data packet from client and verification

	while((connfd = accept(sockfd, (SA*)&cli, &len)) > 0){
		printf("server accept the client...\n");

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

    if (connfd < 0) {
        printf("server accept failed...\n");
        exit(0);
    }

    // Function for chatting between client and server
    // func(connfd);

    // After chatting close the socket
    close(sockfd);
}

void *manageRequest(void *SocketDescriptorClient) {
  printf("thread avviato\n");
  // int status;
	//
  // char buffer[50];
  // int socket_descriptor = *(int *) SocketDescriptorClient;
	//
  // printf("gestisco la richiesta del client socket %d\n", socket_descriptor);
	//
  //   bzero(buffer, 50);
  //   recv(socket_descriptor, buffer, 50, 0);
	//
  //   printf("buffer: %s\n", buffer);
	//
	//
	//
  //   printf("Richiesta del client servita, chiudo la connessione col client\n");


	char buff[MAX];
			int socket_descriptor = *(int *) SocketDescriptorClient;

		  printf("gestisco la richiesta del client socket %d\n", socket_descriptor);

					// read the message from client and copy it in buffer
					read(socket_descriptor, buff, 4);
					// print buffer which contains the client contents
					printf("From client: %s\t To client : \n", buff);

					// and send that buffer to client
					write(socket_descriptor, buff, 4);

	close(socket_descriptor);

}

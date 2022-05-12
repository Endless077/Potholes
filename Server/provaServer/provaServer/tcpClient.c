#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <fcntl.h> // for open
#include <unistd.h> // for close
#include<sys/un.h>

// #define MAX 80
#define PORT 8080
#define SA struct sockaddr

#define SOCKET_NAME "my_server"

void func(int sockfd){
	char buff[] = "ciao";
	 int n;
			 sleep(1);
			 write(sockfd, buff, 4);
			 bzero(buff, sizeof(buff));
			 read(sockfd, buff, sizeof(buff));
			 printf("From Server : %s\n", buff);

}

int main()
{
	int sockfd, connfd;
	struct sockaddr_un servaddr, cli;

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

	// connect the client socket to server socket
	if (connect(sockfd, (SA*)&servaddr, sizeof(servaddr)) != 0) {
		printf("connection with the server failed...\n");
		exit(0);
	}
	else
		printf("connected to the server.. socket %d\n", sockfd);

	// function for chat
	func(sockfd);

	// close the socket
	close(sockfd);
}

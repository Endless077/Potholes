/*Client for testing server funcions*/

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/types.h>
#include<sys/un.h>
#include <arpa/inet.h>
#include<string.h>

#define SOCKET_NAME "my_server"

#define PORT 5678
#define SA struct sockaddr

static int client();

void func(int sockfd){

	//Change with: getAll, getNear, post, threshold
	char buff[] = "getAll";
	char buffRead[50];

	/*getNear*/
	// int n;
	// sleep(1);
	// write(sockfd, buff, sizeof(buff));
	// bzero(buff, sizeof(buff));
	 
	// bzero(buffRead, sizeof(buffRead));
	 
	// read(sockfd, buffRead, 7);
	// printf("buffRead %s\n", buffRead);
	 
	// if(strcmp(buffRead, "Start") == 0)
	// 	write(sockfd, "vale:40.835884:14.248767:0", 27);
	 
	// bzero(buffRead, sizeof(buffRead));
	// do{
	// 	bzero(buffRead, sizeof(buffRead));
	// 	read(sockfd, buffRead, 50);
	// 	printf("From Server : %s\n", buffRead);
	// }while(strcmp(buffRead, "end")!=0);

	/*getAll*/
	// sleep(1);
	// write(sockfd, buff, sizeof(buff));
	// bzero(buff, sizeof(buff));
		 
	// do{
	// 	bzero(buffRead, sizeof(buffRead));
	//  	read(sockfd, buffRead, 50);
	//  	printf("From Server : %s\n", buffRead);
	// }while(strcmp(buffRead, "end")!=0);


	/*threshold*/
	// sleep(1);
	// write(sockfd, buff, sizeof(buff));
	// bzero(buff, sizeof(buff));
	// bzero(buffRead, sizeof(buffRead));
	// read(sockfd, buffRead, 50);
	// printf("From Server : %s\n", buffRead);


	/*put*/
	// int n;
	// sleep(1);
	// write(sockfd, buff, sizeof(buff));
	// bzero(buff, sizeof(buff));

	// bzero(buffRead, sizeof(buffRead));

	// read(sockfd, buffRead, 7);
	// printf("buffRead %s\n", buffRead);

	// if(strcmp(buffRead, "Start") == 0)
	// 	write(sockfd, "NuovoUtente:41.134679:14.172839:", 33);

	// bzero(buffRead, sizeof(buffRead));
	// read(sockfd, buffRead, sizeof(buffRead));
	// printf("buffRead %s\n", buffRead);
	// bzero(buffRead, sizeof(buffRead));

}

int main(void) {
	return client();
}

int client(){
	int sockfd, connfd;
	struct sockaddr_in servaddr, cli;

	// socket create and verification
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1) {
		printf("socket creation failed...\n");
		exit(0);
	}else
		printf("Socket successfully created..\n");
	
	bzero(&servaddr, sizeof(servaddr));

	// assign IP, PORT
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	servaddr.sin_port = htons(PORT);

	// connect the client socket to server socket
	if (connect(sockfd, (SA*)&servaddr, sizeof(servaddr)) != 0) {
		printf("connection with the server failed...\n");
		exit(0);
	}else
		printf("connected to the server.. socket %d\n", sockfd);

	func(sockfd);

	// close the socket
	close(sockfd);
}

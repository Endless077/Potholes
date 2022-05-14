/*  ______  _____     _____  ________  ____  _____  _________
 .' ___  ||_   _|   |_   _||_   __  ||_   \|_   _||  _   _  |
/ .'   \_|  | |       | |    | |_ \_|  |   \ | |  |_/ | | \_|
| |         | |   _   | |    |  _| _   | |\ \| |      | |
\ `.___.'\ _| |__/ | _| |_  _| |__/ | _| |_\   |_    _| |_
 `.____ .'|________||_____||________||_____|\____|  |_____|
 */

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

	//Change setup to send whetever you want
	char buffWrite[100];
	char buffRead[100];

	/*********************************************************************************/

	/*getAll*/
	printf("Testing getAll\n");
	sprintf(buffWrite, "getAll");
	sleep(3);

	write(sockfd, buffWrite, sizeof(buffWrite));
	bzero(buffWrite, sizeof(buffWrite));

	do{
		bzero(buffRead, sizeof(buffRead));
	 	read(sockfd, buffRead, 50);
	 	printf("From Server : %s\n", buffRead);
	}while(strcmp(buffRead, "END")!=0);

	/*Resetting*/
	bzero(buffWrite, sizeof(buffWrite));
	bzero(buffRead, sizeof(buffRead));

	/*********************************************************************************/

	// /*getNear*/
	printf("Testing getNear\n");
	sprintf(buffWrite, "getNear");
	sleep(3);

	write(sockfd, buffWrite, sizeof(buffWrite));

	bzero(buffWrite, sizeof(buffWrite));
	bzero(buffRead, sizeof(buffRead));

	read(sockfd, buffRead, 7);
	printf("BuffRead: %s\n", buffRead);

	if(strcmp(buffRead, "Start") == 0)
		write(sockfd, "vale:40.835884:14.248767:0", 27);

	bzero(buffRead, sizeof(buffRead));

	//do{
		bzero(buffRead, sizeof(buffRead));
		read(sockfd, buffRead, 50);
		printf("From Server : %s\n", buffRead);
	//}while(strcmp(buffRead, "END")!=0);

	/*Resetting*/
	bzero(buffWrite, sizeof(buffWrite));
	bzero(buffRead, sizeof(buffRead));

	/*********************************************************************************/

	/*threshold*/
	printf("Testing threshold\n");
	sprintf(buffWrite, "threshold");
	sleep(3);

	write(sockfd, buffWrite, sizeof(buffWrite));

	bzero(buffWrite, sizeof(buffWrite));
	bzero(buffRead, sizeof(buffRead));

	read(sockfd, buffRead, 50);
	printf("From Server : %s\n", buffRead);

	/*Resetting*/
	bzero(buffWrite, sizeof(buffWrite));
	bzero(buffRead, sizeof(buffRead));

	/*********************************************************************************/

	/*put*/
	printf("Testing put\n");
	sprintf(buffWrite, "put");
	sleep(3);

	write(sockfd, buffWrite, sizeof(buffWrite));

	bzero(buffWrite, sizeof(buffWrite));
	bzero(buffRead, sizeof(buffRead));

	read(sockfd, buffRead, 7);
	printf("BuffRead: %s\n", buffRead);

	if(strcmp(buffRead, "Start") == 0)
		write(sockfd, "User:41.134679:14.172839:", 33);

	bzero(buffRead, sizeof(buffRead));
	read(sockfd, buffRead, sizeof(buffRead));
	printf("buffRead %s\n", buffRead);
	bzero(buffRead, sizeof(buffRead));

	/*Resetting*/
	bzero(buffWrite, sizeof(buffWrite));
	bzero(buffRead, sizeof(buffRead));
}

int main(void) {
	return client();
}

int client(){
	int sockfd;
	struct sockaddr_in servaddr;

	// socket create and verification
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1) {
		perror("socket creation failed...\n");
		exit(EXIT_FAILURE);
	}else
		printf("Socket successfully created..\n");

	bzero(&servaddr, sizeof(servaddr));

	// assign IP, PORT
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	servaddr.sin_port = htons(PORT);

	// connect the client socket to server socket
	if (connect(sockfd, (SA*)&servaddr, sizeof(servaddr)) != 0) {
		perror("connection with the server failed...\n");
		exit(EXIT_FAILURE);
	}else
		printf("connected to the server.. socket %d\n", sockfd);

	func(sockfd);

	// close the socket
	close(sockfd);
	return 0;
}

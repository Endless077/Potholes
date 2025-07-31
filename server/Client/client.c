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
#include <sys/un.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <time.h>
#include <string.h>

#define SOCKET_NAME "my_server"

#define MAX_BUFFER_READ 100

#define PORT 3390
#define SA struct sockaddr

static void client();

//Change setup to send whetever you want
void func(int sockfd, int action){
	
	//Action:
	//- 0 for threshold;
	//- 1 for getAll;
	//- 2 for getNear;
	//- 3 for post;

	char buffWrite[100];
	char buffRead[100];

	switch (action) {
	
	case 0:
		printf("Testing threshold\n");
		sprintf(buffWrite, "threshold");
		sleep(3);
	
		write(sockfd, buffWrite, sizeof(buffWrite));
	
		bzero(buffWrite, sizeof(buffWrite));
		bzero(buffRead, sizeof(buffRead));
	
		read(sockfd, buffRead, sizeof(buffRead));
		printf("From Server : %s\n", buffRead);
	
		/*Resetting*/
		bzero(buffWrite, sizeof(buffWrite));
		bzero(buffRead, sizeof(buffRead));
		break;

	case 1:
		printf("Testing getAll\n");
		sprintf(buffWrite, "getAll");
		sleep(3);

		write(sockfd, buffWrite, sizeof(buffWrite));
		bzero(buffWrite, sizeof(buffWrite));
		
		do{
			bzero(buffRead, sizeof(buffRead));
		  	read(sockfd, buffRead, sizeof(buffRead));
		  	printf("From Server : %s\n", buffRead);
		}while(strcmp(buffRead, "END")!=0);
	
		/*Resetting*/
		bzero(buffWrite, sizeof(buffWrite));
		bzero(buffRead, sizeof(buffRead));
		break;

	case 2:
		printf("Testing getNear\n");
		sprintf(buffWrite, "getNear");
		sleep(3);
	
		write(sockfd, buffWrite, sizeof(buffWrite));
	
		bzero(buffWrite, sizeof(buffWrite));
		bzero(buffRead, sizeof(buffRead));
	
		read(sockfd, buffRead, 6);
		printf("BuffRead: %s\n", buffRead);
	
		if(strcmp(buffRead, "START\r") == 0)
			write(sockfd, "User:40.835884:14.248767:0", 27);
	
		bzero(buffRead, sizeof(buffRead));
	
		do{
			bzero(buffRead, sizeof(buffRead));
			read(sockfd, buffRead, sizeof(buffRead));
			printf("From Server : %s\n", buffRead);
		}while(strcmp(buffRead, "END")!=0);
	
		/*Resetting*/
		bzero(buffWrite, sizeof(buffWrite));
		bzero(buffRead, sizeof(buffRead));
		break;

	case 3:
		printf("Testing post\n");
		sprintf(buffWrite, "post");
		sleep(3);
	
		write(sockfd, buffWrite, sizeof(buffWrite));
	
		bzero(buffWrite, sizeof(buffWrite));
		bzero(buffRead, sizeof(buffRead));
	
		read(sockfd, buffRead, 6);
		printf("BuffRead: %s\n", buffRead);
	
		if(strcmp(buffRead, "START\r") == 0)
			write(sockfd, "User:41.134679:14.172839:", 26);
	
		bzero(buffRead, sizeof(buffRead));
		read(sockfd, buffRead, sizeof(buffRead));
		printf("BuffRead: %s\n", buffRead);
		bzero(buffRead, sizeof(buffRead));
	
		/*Resetting*/
		bzero(buffWrite, sizeof(buffWrite));
		bzero(buffRead, sizeof(buffRead));
		break;

	default:
		break;
	}
}

int main(int argc, char *argv[]) {

	int lower = 0, upper = 3, count;

	if(argc != 2) {
		printf("Only one args [number of clients]");
    	exit(EXIT_FAILURE);
	}

	count = strtol(argv[1], NULL, 10);
	srand(time(0));

	int action;

	for(int i = 0; i<count; i++) {
		action = (rand() % (upper - lower + 1)) + lower;
		client(action);
	}
	
	return 0;
}

void client(int action) {
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

	func(sockfd, action);

	// close the socket
	close(sockfd);
}

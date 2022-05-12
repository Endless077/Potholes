#include<stdio.h>
#include<errno.h>
#include<stdlib.h>
#include<unistd.h>
#include<sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>

#include<sys/un.h>
#include<string.h>

#define SOCKET_NAME "my_server"

#define PORT 5678
#define SA struct sockaddr


static int client();

void func(int sockfd){
	char buff[] = "post";
	char buffRead[50];

	/*getNear*/
	 // int n;
		// 	 sleep(1);
		// 	 write(sockfd, buff, sizeof(buff));
		// 	 bzero(buff, sizeof(buff));
	 //
		// 	 bzero(buffRead, sizeof(buffRead));
	 //
		// 	 read(sockfd, buffRead, 2);
		// 	 printf("buffRead %s\n", buffRead);
	 //
		// 	 if(strcmp(buffRead, "ok") == 0)
		// 	 		write(sockfd, "vale:40.835884:14.248767:0", 27);
	 //
		// 	 bzero(buffRead, sizeof(buffRead));
		// 	 do{
		// 		 bzero(buffRead, sizeof(buffRead));
		// 	 	read(sockfd, buffRead, 50);
		// 	 	printf("From Server : %s\n", buffRead);
		//  }while(strcmp(buffRead, "end")!=0);


		/*put*/
		 int n;
		sleep(1);
		write(sockfd, buff, sizeof(buff));
		bzero(buff, sizeof(buff));

		bzero(buffRead, sizeof(buffRead));

		read(sockfd, buffRead, 2);
		printf("buffRead %s\n", buffRead);

		if(strcmp(buffRead, "ok") == 0)
			write(sockfd, "Mariano:39.835884:13.248767:", 28);

		bzero(buffRead, sizeof(buffRead));
		read(sockfd, buffRead, sizeof(buffRead));
		printf("buffRead %s\n", buffRead);
		bzero(buffRead, sizeof(buffRead));

}

int main(void){
return client();
}

int client(){
  int sockfd, connfd;
	// struct sockaddr_un servaddr, cli;
	struct sockaddr_in servaddr, cli;

	// socket create and verification
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1) {
		printf("socket creation failed...\n");
		exit(0);
	}
	else
		printf("Socket successfully created..\n");
	bzero(&servaddr, sizeof(servaddr));

	// assign IP, PORT
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	servaddr.sin_port = htons(PORT);
  // servaddr.sun_family = AF_LOCAL;
  // strcpy(servaddr.sun_path, SOCKET_NAME);

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

/*Server*/
#include<sys/socket.h>
#include<stdio.h>
#include<sys/un.h>
#include<unistd.h>
#include<signal.h>

int main() {

  int server_socket_descriptor, client_socket_descriptor;
  int server_len, client_len;
  struct sockaddr_un server_addr;
  struct sockaddr_un client_addr;

  /* TODO generale:
  set server_addr.sun_family and sun_path, socket(), bind(), listen(), while cicle*/
  

 return 0;
}

#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h> 
#include <string.h> 
#include <sys/types.h> 
#include <sys/socket.h> 
#include <arpa/inet.h> 
#include <netinet/in.h> 
#define BUFSIZE 200

int main(int argc, char *argv[]) {

	if (argc != 2) {
		fprintf(stderr, "use: %s <port>\n", argv[0]);
		exit(1);
	}
	int port = atoi(argv[1]);

	struct sockaddr_in servaddr;
	int sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1) {
		printf("socket creation failed...\n");
		exit(0);
	}
	bzero(&servaddr, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(port);
	servaddr.sin_addr.s_addr = INADDR_ANY;//localhost
		
	if (connect(sockfd, (struct sockaddr*)&servaddr, sizeof(servaddr)) != 0) {
		printf("connection with the server failed...\n");
		close(sockfd);
		exit(0);
	} else	printf("connected to the server..\n");
		
	char input[BUFSIZE];
	for(;;){
		printf("Insert string to be echoed:\n");
		bzero(input, BUFSIZE);
		fgets(input, BUFSIZE, stdin);
		
		if (strncmp(input, "exit", 4) == 0) {
			puts(":: EXIT ::");
			break;
		}

		int n = write(sockfd, input, strlen(input));
		if (n < 0)
			perror("ERROR writing to socket");

		bzero(input, BUFSIZE);
		n = read(sockfd, input, BUFSIZE);
		if (n < 0) 
		  perror("ERROR reading from socket");
		printf("Echo from server: %s", input);
	}
	close(sockfd);
    return 0;
}   


#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h> 
#include <string.h> 
#include <sys/types.h> 
#include <sys/socket.h> 
#include <arpa/inet.h> 
#include <netinet/in.h> 

char rnd[6];//initialized with 0's
char request[8] = {0};

void rnd_int(){
	for(int i = 0; i < 6 ; i++){
		rnd[i] = '0' + rand() % 10;
	}
}

int main()
{
	struct sockaddr_in servaddr = {0};
	int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
	if(sockfd == -1)
	{
		perror("failed to create socket");
		exit(EXIT_FAILURE);
	}
	
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(54345);
	servaddr.sin_addr.s_addr = INADDR_ANY;
	char* input;
	for(;;){
		printf("1. número total de sessões estabelecidas no porto 54321\n");
		printf("2. número total de sessões estabelecidas no porto 56789\n");
		printf("3. número total de caracteres recebidos em ligações para o porto 54321\n");
		printf("4. número total de caracteres recebidos em ligações para o porto 56789\n\n");
		scanf("%s", input); 
		if (strncmp(input, "exit", 4) == 0) {
			puts(":: EXIT ::");
			break;
		}

		if(strlen(input) > 1 || *input > '4'){
			fprintf(stderr,"Invalid input!!!\n");
		}else{
			request[0] = 'Q';
			strcat(request, input);
			rnd_int();
			strcat(request, rnd);
			
			int len = sendto(sockfd, (const char *)request, strlen(request), 0, (const struct sockaddr *)&servaddr, sizeof(servaddr));
			if(len ==-1)
				perror("failed to send");

			int received_bytes;
			char buffer[1024];
			int slen = sizeof(servaddr);
			if((received_bytes = recvfrom(sockfd, buffer, sizeof(buffer), 0, (struct sockaddr *)&servaddr, &slen)) == -1){
				perror("Recv ");
				break;
			}

			buffer[received_bytes] = '\0';
			printf("Received : %s\n", buffer);
			memset(request,0,8);
		}
	}    
	close(sockfd);
    return 0;
}

#include <stdio.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include "syscalls.h"

int main(int argc, char *argv[]) {	
	if( argc == 2) {
		//Get pid
		int pid = xgetpid();
		//Transform pid to a string
		char buffer[200];
		sprintf(buffer, "%d", pid);
		//Open file
		int fd = xopen(argv[1], O_WRONLY | O_CREAT, 0666);
		if(fd < 0){
			perror("Error in opening file.");
			exit(1);
		}
		//Cat string to write in file
		strcat(buffer, "\n");
		strcat(buffer, STUDENTS);
		//Write in file
		size_t write = xwrite(fd, buffer, strlen(buffer));
		if(write < 0){
			perror("Error in writing file.");
			xclose(fd);
			exit(1);
		}
	//Close file
	xclose(fd);
		
   }
   else if( argc > 2 ) {
      printf("Too many arguments supplied.\n");
   }
   else {
      printf("One argument expected.\n");
   }
   return 0;
}

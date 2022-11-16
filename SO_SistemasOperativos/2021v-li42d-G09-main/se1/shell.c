#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <fcntl.h>

#define BUFFER_SIZE 200
#define CD_SIZE 200
#define TRUE 1

int pointer_array_length(char** parameters);
int execute_command(char *args[]);
int execute_command_redirect(char **parameters);
int execute_command_piped(char**args, int pipes);
int close_pipes(int p, int *fd[]);
void create_process(char** args, char**pid,  int p, int *fd[]);
int close_connections(int p, int *fd[], int pipes);

int type_prompt(){
	char cwd[CD_SIZE];
   	if (getcwd(cwd, sizeof(cwd)) != NULL) {
		printf("\033[0;31m");//change text colour to red 
		printf("%s$ ", cwd);
		printf("\033[0m");	//reset text colour	
   	}else {
   		perror("getcwd() error");
   		return 1;
   	}   	
	return 0;
}

void free_memory(char** parameters){
	for(int i=0; parameters[i] != NULL; i++){
		free(parameters[i]);
	}
}

//separa por pipes
void read_command(char **args){
	char *string = (char *)malloc(BUFFER_SIZE * sizeof(char));
	if(!string){
		fprintf(stderr, "Error: memory allocation failure -> read_command \n");
		exit(1);
	}
	fgets(string, BUFFER_SIZE, stdin);
	char * token = strtok(string, "|");
	int i = 0;
	while(token != NULL) {
		args[i] = (char *)malloc(BUFFER_SIZE * sizeof(char));
		strcpy(args[i],"/bin/");
		if(i) token++;
		strcat(args[i], token);
		args[i][strcspn(args[i], "\n")] = 0; //take the \n char
		token = strtok(NULL, "|");
		i++;
	}
	args[i] = NULL;
	free(string);
}

void break_by_spaces(char **parameters, char *args){
	char *token = strtok(args, " ");
	int i = 0;
	while(token != NULL) {
		parameters[i] = (char *)malloc(BUFFER_SIZE * sizeof(char));
		strcpy(parameters[i], token);
		parameters[i][strcspn(parameters[i], "\n")] = 0; //take the \n char
		token = strtok(NULL, " ");
		i++;
	}
	parameters[i] = NULL;
}

int pointer_array_length(char** parameters){
	int counter = 0;
	while(parameters[counter] != NULL){
		counter++;
	}
	return counter;
}

int execute_command(char *args[]){
	pid_t pid = fork();
	if (pid == -1){
        perror("error fork in execute_command.");
        exit(1);
    }
	if (pid!= 0) { //PARENT
		int res;
		waitpid(pid, &res, 0);
	} else { //CHILD
		execve(args[0], args, 0);
	}
	return 1;
}

int execute_command_redirect(char **parameters){
	FILE *fptr = fopen(parameters[3],"w");
	if(fptr == NULL){
      fprintf(stderr, "Error: open file failed -> execute_command_redirect\n");
      exit(1);             
    }
	pid_t pid = fork();
	
	if (pid == -1){
        perror("error fork in execute_command_redirect.");
        exit(1);
    }
	
	if (pid!= 0) { //PARENT
		int res;
		waitpid(pid, &res, 0);
		fclose(fptr);
	} else { //CHILD
		parameters[2] = NULL;
		dup2(3,1);
		execve(parameters[0], parameters, 0);
	}
	return 1;
}

int execute_command_piped(char** args, int pipes){
	int flag = 0;
	int pid_arr[pipes + 1];
	int fd[pipes][2];
    pid_t pid = 0;	
	char **params = (char **)malloc(BUFFER_SIZE * sizeof(char *));
	if(params == NULL){
		fprintf(stderr, "Error: memory allocation failure -> read_command \n");
		exit(1);
	}
	
	for(int i = 0; i <= pipes; i++){
		break_by_spaces(params, args[i]);
		if(i != pipes) pipe(fd[i]);
		
		pid = fork();
		pid_arr[i] = pid;
		if (pid == -1){
			perror("error fork in execute_command_piped.");
			exit(1);
		} else if(pid == 0){
			if(i == 0){
				close(fd[i][0]);
				dup2(fd[i][1], 1); 
				close(fd[i][1]);
			}else if(i > 0 && i < pipes) {
				dup2(fd[i-1][0], 0);
				close(fd[i-1][0]);
				dup2(fd[i][1], 1);
				close(fd[i][1]);
			}else {
				if((pointer_array_length(params) > 0) && (strcmp(params[1], ">") == 0)){
					flag = 1;
					char *aux[2] = {params[0], NULL};
					//0666 têm a ver com as permissões
					int desc = open(params[2],O_WRONLY | O_CREAT, 0666);
					if(desc < 0){
						fprintf(stderr, "Error: open file failed \n");
						exit(1);             
					}
					dup2(fd[i-1][0], 0);
					dup2(desc, 1);
					execve(params[0], aux, 0);
					close(fd[i-1][0]);
					close(desc);
				} else {
					dup2(fd[i-1][0], 0);
					close(fd[i-1][0]);
				}
			}
			if(flag == 0){
				execve(params[0], params, 0);
			}
		} 
		if(i < pipes) close(fd[i][1]);
		if(i > 0) close(fd[i-1][0]);
		free_memory(params);
	}
		
	for(int i = 0; i <= pipes; i++){
		waitpid(pid_arr[i], NULL, 0);
	}	
	free(params);
	
    return 0;
}

int process_command(char **args){
	if(strcmp(args[0], "/bin/exit") == 0)
		return 0;
	int args_length = pointer_array_length(args);
	
	if(args_length == 1){
		char **parameters = (char **)malloc(BUFFER_SIZE * sizeof(char *));
		if(!parameters){
			fprintf(stderr, "Error: memory allocation failure -> process_command \n");
			exit(1);
		}
		break_by_spaces(parameters, *args);
		int params_length = pointer_array_length(parameters);
		if(params_length > 2 && strcmp(parameters[2], ">") == 0) 
			execute_command_redirect(parameters);
		else execute_command(parameters);
		
		free_memory(parameters);
		free(parameters);
	} else {
		execute_command_piped(args, args_length - 1);	
	}
	return 1;
}

int main() {
	char **args = (char **)malloc(BUFFER_SIZE * sizeof(char *));
	if(!args){
		fprintf(stderr, "Error: memory allocation failure -> main \n");
		exit(1);
	}
	int exit_cond = TRUE;
	while (exit_cond) {
		type_prompt();
		read_command(args);
		exit_cond = process_command(args);
		free_memory(args);
	}
	free(args);
	return 0;	
}

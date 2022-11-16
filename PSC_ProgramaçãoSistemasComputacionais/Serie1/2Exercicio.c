#include <stdio.h>
#include <stdlib.h>
#include <string.h>

size_t read_line(FILE *fd, char *buffer, size_t buffer_size){
    if(fd == NULL){
        fprintf(stderr, "%s \n","The pointer to the file is NULL.\n" );
        exit(1);
    }
    char c = fgetc(fd);
    if(c == EOF){
		fprintf(stderr, "%s \n","End of file found.\n" );
        return -1;
    }
    size_t i = 0;
    while(c != '\n' && i < buffer_size && c != '\0'){
        buffer[i++] = c;
        c = fgetc(fd);		
    }
    buffer[i] = '\0';
    fclose(fd);
    return i;
}
char line[100];

#define ARRAY_SIZE(a) (sizeof(a)/sizeof(a)[0])

int main(){
    FILE *file_descriptor = fopen("input.txt", "r");
    int size = read_line(file_descriptor++, line, ARRAY_SIZE(line));
    FILE *file_output = fopen("output.txt", "w");
    fprintf(file_output, "%s\n", line);
    fclose(file_output);
    return 0;
}



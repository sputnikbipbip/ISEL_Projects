#include <stdlib.h>


int data = 200;
char info[8192];

void get_info(){
	char * test = malloc(sizeof(char)* 16000);
}

int get_data(){
	return data;
}

int * get_data_address(){
	return &data;
}

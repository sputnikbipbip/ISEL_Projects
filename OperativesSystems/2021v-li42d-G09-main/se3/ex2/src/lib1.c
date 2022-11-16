#include <stdlib.h>
#include <sys/mman.h>
#include <fcntl.h>

	
int data = 200;
char info[8192];

void get_info(){
	void * ptr = mmap(NULL, 16384, PROT_READ|PROT_WRITE, MAP_PRIVATE, -1, 0);
}

int get_data(){
	return data;
}

int * get_data_address(){
	return &data;
}

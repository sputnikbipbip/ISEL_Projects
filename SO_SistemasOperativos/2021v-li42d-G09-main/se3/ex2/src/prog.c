#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <dlfcn.h>

#define DATA_SIZE 16*1024*1024

char info[DATA_SIZE];
char data[DATA_SIZE] = {1};

int main() {
    printf("PID: %u\n", getpid());
    void * ptr = info;
    void * ptr1 = data;
    printf(".bss  address = %p\n", ptr);
    printf(".data address = %p\n", ptr1);
    printf("#1 (press ENTER to continue)"); getchar();
	
    // a
    for(int i = 0; i < DATA_SIZE - 1; i++){
		info[i] = 'c';
		char trabalha = info[i];
	}

    printf("#2 (press ENTER to continue)"); getchar();

    // b
    char * clean = malloc(sizeof(char) * DATA_SIZE);
    

    printf("#3 (press ENTER to continue)"); getchar();
	free(clean);
    // c
    char * newLocation = malloc(sizeof(char) * DATA_SIZE);

    printf("#4 (press ENTER to continue)"); getchar();
    
    for(int i = 0; i < 16000; i++) {
		newLocation[i] = 'c';
	}

    // d

    printf("#5 (press ENTER to continue)"); getchar();
	free(newLocation);
    // e
    
    //void * libex1 = dlopen("/usr/lib/libwvbase.so.4.6", RTLD_LOCAL | RTLD_NOW);
    //void * libex2 = dlopen("/usr/lib/liboath.so.0.1.3", RTLD_LOCAL | RTLD_NOW);
    void * lib1 = dlopen("./lib1.so", RTLD_LOCAL | RTLD_NOW);
	void * lib2 = dlopen("./lib2.so", RTLD_LOCAL | RTLD_NOW);
    printf("lib1 loaded at %p\n", lib1);
    printf("lib2 loaded at %p\n", lib2);

    printf("#6 (press ENTER to continue)"); getchar();

    // f
    //int    -> retorno
    //(*go1) -> ponteiro que aponta para a caractere da função
    //(void) -> tipo de parâmetro que a função aceita
    void * (*go1)(void) = dlsym(lib1, "get_info");
    //void res1 = 
    void * auxPtr = go1();
    void * (*go2)(void) = dlsym(lib2, "get_info");
	//void res2 = 
	void * auxPtr1 = go2();
	printf("First get = %p, Second get = %p\n", auxPtr, auxPtr1);
    printf("#7 (press ENTER to continue)"); getchar();

    // g
    
    //shared reading 
	if(fork() == 0) {
			//char * res3 = 
			go1();
			//char * res4 = 
			go2();
	}else {
			puts("cmon child");
	}
		
    printf("END (press ENTER to continue)"); getchar();
    return 0;
}


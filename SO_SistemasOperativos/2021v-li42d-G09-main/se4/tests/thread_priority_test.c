#include <stdio.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>
#include <assert.h>
#include "uthread.h"


void func1(void * arg) {

	printf("Start func1\n");
	
	ut_yield();
	
	printf("End func1\n");

}

void func2(void * arg) {
	const char* name = (const char*) arg;
	printf("%s\n", name);

}

void test() {
	printf("\n :: Test - BEGIN :: \n\n");
	
	ut_create(func1, NULL, 1);
	ut_create(func2, "func2", 3);
	ut_create(func2, "func3", 2);
	ut_create(func2, "func4", 1);
	
	ut_run();

	printf("\n\n :: Test - END :: \n");
}



int main () {
	
	/*
	 * expected:
	 * func2
	 * func3
	 * func1
	 * func4
	 * func1
	 * */
	
	ut_init();
	
	test();	
	ut_end();
	return 0;
}


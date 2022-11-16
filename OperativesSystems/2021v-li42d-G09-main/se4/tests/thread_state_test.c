#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>

#include "uthread.h"

#define DEBUG

#define MAX_THREADS 10

uthread_t *uthread_1;
uthread_t *uthread_2;
uthread_t *uthread_3;
uthread_t *uthread_4;

void func1(void * arg) {

	printf("Start func1\n");
	
	assert(ut_state(uthread_1) == 1);
	assert(ut_state(uthread_2) == 0);
	assert(ut_state(uthread_3) == 0);
	assert(ut_state(uthread_4) == 0);
	
	ut_yield();
	printf("After ut_yield() in func1\n");
	
	assert(ut_state(uthread_1) == 1);
	assert(ut_state(uthread_3) == 2);

	//nao vale a pena testar o state da thread_2 aqui, pois ja terminou
	ut_activate(uthread_3);
	printf("After ut_activate(uthread_3) in func1\n");
	assert(ut_state(uthread_1) == 1);
	assert(ut_state(uthread_3) == 0);
	printf("End func1\n\n");
}

void func2(void * arg) {
	printf("\nfunc2\n");
	assert(ut_state(uthread_1) == 0);
	assert(ut_state(uthread_2) == 1);
	assert(ut_state(uthread_3) == 0);
	assert(ut_state(uthread_4) == 0);
	printf("End func2\n\n");
}

void func3(void * arg) {
	printf("\nfunc3\n");
	ut_deactivate();
	assert(ut_state(uthread_3) == 1);
	printf("End func3\n\n");
}

void func4(void * arg) {
	printf("\nfunc4\n");
	
	assert(ut_state(uthread_1) == 0);
	assert(ut_state(uthread_3) == 2);
	assert(ut_state(uthread_4) == 1);
	printf("End func4\n\n");	
}

void test() {
	printf("\n :: Test - BEGIN :: \n\n");
	
	uthread_1 =  ut_create(func1, NULL, 2);
	uthread_2 = ut_create(func2, NULL, 2);
	uthread_3 = ut_create(func3, NULL, 2);
	uthread_4 = ut_create(func4, NULL, 2);
	
	ut_run();

	printf("\n\n :: Test - END :: \n\n");
}

int main () {
	ut_init();
	test();	
	ut_end();
	return 0;
}




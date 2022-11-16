#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>

#include "usynch.h"

void f1_run(){
	printf("f1_run begin\n");
	printf("f1_run end\n\n");
}



void f2_wait(void * args){
	barrier_t * barrier = (barrier_t*) args;
    printf("f2_wait begin\n");
    int res = barrier_await(barrier);
    printf("f2_wait ended with: %d\n\n", res);
}

void f3_wait(void * args){
	barrier_t * barrier = (barrier_t*) args;
    printf("f3_wait begin\n");
    int res = barrier_await(barrier);
    printf("f3_wait ended with: %d\n\n", res);
}


void f4_wait(void * args){
	barrier_t * barrier = (barrier_t*) args;
    printf("f4_wait begin\n");
    int res = barrier_await(barrier);
    printf("f4_wait ended with: %d\n\n", res);
}

void barrier_test()
{
	barrier_t barrier;
	
	printf("Barrier test  begin\n\n");
	
	barrier_init(&barrier, 3);
	
	ut_create(f1_run, NULL, 3);
	ut_create(f2_wait, &barrier, 2);
	ut_create(f3_wait, &barrier, 3);
	ut_create(f4_wait, &barrier, 1);
	
	/*
	 * expected:
		f1_run begin
		f1_run end

		f3_wait begin
		f2_wait begin
		f4_wait begin
		f4_wait ended with: 2

		f3_wait ended with: 1

		f2_wait ended with: 0
	 *
	 * */

	
	ut_run();
	
	printf("event test  end\n");
}




int main()
{
	ut_init();
	barrier_test();
	ut_end();
    return 0;
}

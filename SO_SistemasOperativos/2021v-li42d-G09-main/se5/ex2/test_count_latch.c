#include <unistd.h>
#include <stdio.h>

#include "count_latch.h"

void * source_thread(void * args) {
	printf("source_thread \n");
	count_latch_t * latch = (count_latch_t *) args;
	cl_up(latch); 
	printf("worker_threads: %d\n", latch->worker_threads);
	printf("thread id: %ld\n\n", pthread_self());
	return 0;
}

void * latch_down(void * args) {
	count_latch_t * latch = (count_latch_t *) args;
	printf("latch_down \n");
	cl_down(latch);
	printf("worker_threads: %d\n", latch->worker_threads);
	printf("thread id: %ld\n\n", pthread_self());
	return 0;
}

void * wait(void * args) {
	count_latch_t * latch = (count_latch_t *) args;
	printf("wait \n");
	cl_wait_all(latch);
	printf("worker_threads: %d\n", latch->worker_threads);
	printf("thread id: %ld\n\n", pthread_self());
	return 0;
}

int main() {
	pthread_t source1, source2, source3, source4, source5;
	count_latch_t latch;
	cl_init(&latch, 4);
	printf(":: START ::\n");
	pthread_create(&source1, NULL, source_thread, &latch);
	pthread_create(&source2, NULL, source_thread, &latch);
	pthread_create(&source3, NULL, source_thread, &latch);
	pthread_create(&source4, NULL, source_thread, &latch);
	pthread_create(&source5, NULL, latch_down, &latch);
	


		

	

	pthread_join(source1, NULL);
	pthread_join(source2, NULL);
	pthread_join(source3, NULL);
	pthread_join(source4, NULL);
	pthread_join(source5, NULL);
	
	printf(":: END ::\n");
	
	return 0;

}

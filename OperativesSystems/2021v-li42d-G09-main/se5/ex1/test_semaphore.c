#include "semaphore.h"
#include <errno.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#define N 10000


void * run_with_timeout(void * args) {
	semaphore_t * sem = (semaphore_t *)args;
	printf("::with timeout::\n");
	semaphore_acquire_timed(sem, 1, 5000);
	int counter = 0;
	for(int i = 0; i < N; ++i) {
		counter = i;
	}
	printf("Counter = %d\n", counter);
	printf("Running thread id = %ld \n\n", pthread_self());
	return NULL;
}

void * run_without_timeout(void * args) {
	semaphore_t * sem = (semaphore_t *)args;
	semaphore_acquire(sem, 2);
	return NULL;
}

int main(int argc, char **argv) {
	pthread_t threads[2];
	semaphore_t sem;
	semaphore_init(&sem, 2);
	for(int i = 0; i < 2; i++) {
		pthread_create(&threads[i], NULL, run_with_timeout, &sem);
	}
	for(int i = 0; i < 2; i++) {
		if (pthread_join(threads[i], NULL) != 0) {
			printf("Failed to join thread = %ld \n", pthread_self());
			perror("Failed to terminate thread");
		}
	}
	printf("::Test Done::\n");
	return 0;
}

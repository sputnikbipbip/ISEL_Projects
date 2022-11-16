#include "semaphore.h"
#include <errno.h>
#include <time.h>

void semaphore_init(semaphore_t * sem, int initial) {
	//initialises the mutex passed with default mutex attributes
	pthread_mutex_init(&sem->lock, NULL);
	//initialises the condition variable passed with default condition attributes
	pthread_cond_init(&sem->waiters, NULL);
	sem->units = initial;
}

void semaphore_acquire(semaphore_t * sem, int units) {
	pthread_mutex_lock(&sem->lock);
	{
		while(sem->units < units) {
			pthread_cond_wait(&sem->waiters, &sem->lock);
		}
		sem->units -= units;
	}
	pthread_mutex_unlock(&sem->lock);
}

bool semaphore_acquire_timed(semaphore_t * sem, int units, long millis) {
	//talvez possa ser feito após definir valor de ts
	int res = 0;
	pthread_mutex_lock(&sem->lock);
	{
		struct timespec ts;
		ts.tv_sec = time(NULL) + (millis / 1000);
		ts.tv_nsec = (millis % 1000) * 1000000L;
		while(sem->units < units && res != ETIMEDOUT && res != EINVAL && res != EPERM) {
			res = pthread_cond_timedwait(&sem->waiters, &sem->lock, &ts);
		}
		if (!res) {
			sem->units -= units;
		} 
	}
	pthread_mutex_unlock(&sem->lock);
	printf("Res = %d\n", res);
	return res == 0 ? true : false;
}

void semaphore_release(semaphore_t * sem, int units) {
	pthread_mutex_lock(&sem->lock);
	{
		sem->units += units;
		//liberta todas as threads que estavam à espera de unidades
		pthread_cond_broadcast(&sem->waiters);
	}
	pthread_mutex_unlock(&sem->lock);
}

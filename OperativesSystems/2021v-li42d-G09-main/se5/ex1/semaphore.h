#pragma once

#include <stdio.h>
#include <pthread.h>
#include <stdbool.h>

typedef struct semaphore{
	pthread_mutex_t lock;
	pthread_cond_t waiters;
	int units;
} semaphore_t;

// inicia o semáforo com o número de unidades especificado em initial.
void semaphore_init(semaphore_t * sem, int initial);

// adquire, com timeout infinito, o número de unidades especificado em units.
void semaphore_acquire(semaphore_t * sem, int units);

// adquire, com timeout de millismilisegundos, o número de unidades especificado em units.
bool semaphore_acquire_timed(semaphore_t * sem, int units, long millis);

// entrega o número de unidades ao semáforo especificado em units.
void semaphore_release(semaphore_t * sem, int units);

#include "count_latch.h"

// Inicia o count latch com o nível máximo de paralelismo especificado em par_level. Caso o valor de 
// par_level seja 0, considera-se não haver limite ao nível de paralelismo.
void cl_init(count_latch_t *latch, int par_level) {
	//initialises the mutex passed with default mutex attributes
	pthread_mutex_init(&latch->lock, NULL);
	//initialises the cond passed with default cond attributes
	pthread_cond_init(&latch->reached_zero, NULL);
	pthread_cond_init(&latch->reached_par_level, NULL);
	
	latch->par_level = par_level;
	latch->worker_threads = 0;
}

void cl_cleanup(count_latch_t * latch) {
	pthread_mutex_destroy(&latch->lock);
	pthread_cond_destroy(&latch->reached_par_level);
	pthread_cond_destroy(&latch->reached_zero);
}

// Espera que a contagem das unidades de trabalho seja 0.
void cl_wait_all(count_latch_t *latch) {
	pthread_mutex_lock(&latch->lock);
	{
		while (latch->worker_threads > 0) {
			pthread_cond_wait(&latch->reached_zero, &latch->lock);
		}
	}
	pthread_mutex_unlock(&latch->lock);
}

// Contabiliza mais uma unidade de trabalho. Bloqueia a thread invocante caso o nível máximo de 
// paralelismo tenha sido atingido.
void cl_up(count_latch_t *latch) {
	pthread_mutex_lock(&latch->lock);
	{
		while (latch->worker_threads  == latch->par_level) {
			pthread_cond_wait(&latch->reached_par_level, &latch->lock);
		}
		if(latch->worker_threads < latch->par_level) {
			latch->worker_threads++;
		}
	}
	pthread_mutex_unlock(&latch->lock);
}

// Contabiliza menos uma unidade de trabalho. Caso se tenha descido abaixo do nível máximo de paralelismo 
// liberta umas das threads bloqueadas na operação cl_up. Caso o número de unidades de trabalho chegue a 
// 0 desbloqueia todas as threads bloqueadas na operação cl_wait_all.
void cl_down(count_latch_t *latch) {
	pthread_mutex_lock(&latch->lock);
	{
		if(latch->worker_threads > 0){
			latch->worker_threads--;
		}
		if(latch->worker_threads == 0){
			pthread_cond_broadcast(&latch->reached_zero);	
		}else if(latch->worker_threads <= latch->par_level) {
			pthread_cond_signal(&latch->reached_par_level);
		}
				
	}
	pthread_mutex_unlock(&latch->lock);
}

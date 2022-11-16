#include <pthread.h>

/*
 * sincronizador count_latch_t, utilizável em cenários em que uma ou mais threads distribuem, 
 * de forma dinâmica, trabalho por worker threads, e esperam pela sua conclusão, com eventual 
 * limite do nível de paralelismo.
 * */

typedef struct count_latch {
	pthread_mutex_t lock;
	pthread_cond_t reached_zero; //condition variable: when counter reach 0
	pthread_cond_t reached_par_level; //condition variable: when counter reach par_level
	int worker_threads;
	int par_level; //máximo de worker threads a trabalhar em simultâneo
} count_latch_t;

// Inicia o count latch com o nível máximo de paralelismo especificado em par_level. Caso o valor de 
// par_level seja 0, considera-se não haver limite ao nível de paralelismo.
void cl_init(count_latch_t *latch, int par_level);

// Espera que a contagem das unidades de trabalho seja 0.
void cl_wait_all(count_latch_t *latch);

// Contabiliza mais uma unidade de trabalho. Bloqueia a thread invocante caso o nível máximo de 
// paralelismo tenha sido atingido.
void cl_up(count_latch_t *latch);

// Contabiliza menos uma unidade de trabalho. Caso se tenha descido abaixo do nível máximo de paralelismo 
// liberta umas das threads bloqueadas na operação cl_up. Caso o número de unidades de trabalho chegue a 
// 0 desbloqueia todas as threads bloqueadas na operação cl_wait_all.
void cl_down(count_latch_t *latch);

void cl_cleanup(count_latch_t * latch);

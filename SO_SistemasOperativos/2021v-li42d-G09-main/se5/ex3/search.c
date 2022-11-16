#include <stdio.h>

#include <assert.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <dirent.h>
#include <strings.h>

#include "search.h"
#include "thread_pool.h"
#include "count_latch.h"

#define NTHREADS 4



typedef struct file_task {
	pthread_mutex_t lock;
	count_latch_t * c_latch;
	search_result_t search_result;
	const char * path;
	const char * to_find;
	
} file_task_t;

thread_pool_t tpool;
count_latch_t c_latch;
file_task_t tasks[NTHREADS];

// utilitary functions

// read a line from a file 
static int readline(FILE *f, char line[], int capacity) {
	int i = 0, c;
	while (i < capacity - 1 && (c = fgetc(f)) != EOF && c != '\n')
		line[i++] = c;
	line[i] = 0;
	while (c != EOF && c != '\n') c = fgetc(f);
 	return (i != 0 || c != EOF) ? i : -1;
}

// check if the string "suffix" ends the string "s"
static bool strends(const char *s, const char *suffix) {
	int is = strlen(s) - 1, ip = strlen(suffix) - 1;
	
	while (is >= 0 && ip >= 0 && s[is] == suffix[ip]) { is--; ip--; }

	return ip == -1;
}


// 
// auxiliary functions for search
//

// create a structure that will contain the search results
void search_result_init(search_result_t *res, int limit) {
	bzero(res, sizeof(search_result_t));
	res->results = (fresult_t *) malloc(sizeof(fresult_t)*limit);
	res->results_capacity = limit;
	
	cl_init(&c_latch, NTHREADS);
	for (size_t n = 0; n < NTHREADS; ++n) {
		pthread_mutex_init(&tasks[n].lock, NULL);
		tasks[n].c_latch = &c_latch;//all thread have the same c latch
		bzero(&tasks[n].search_result, sizeof(search_result_t));
		tasks[n].search_result.results = (fresult_t *) malloc(sizeof(fresult_t)*limit);
		tasks[n].search_result.results_capacity = limit;
		
	}
}

// destroy the search results
void search_result_destroy(search_result_t *res) {
	
	
	for(int i = 0; i < NTHREADS; i++){
		//todo: check it is needed to free paths
		for (int j = 0; j < tasks[i].search_result.total_results; ++j)  
			free(tasks[i].search_result.results[j].path);
		free(tasks[i].search_result.results);
	}
	//todo:
	for (int i = 0; i < res->total_results; ++i)  
		free(res->results[i].path);
	free(res->results);
}


/*
   if *fes is NULL force it to point to the next free fresult_t in res
   return false if no free fresult_t exists on res
*/
static bool enforce_result(search_result_t * res, fresult_t ** fres) {
	if (*fres != NULL) return true;
	if (res->total_results == res->results_capacity) return false;
	*fres = res->results + res->total_results;
	res->total_results++;
	(*fres)->count = 0;
	return true;
}

/*
 * search a word in the file with the specified name collecting
 * the result in res. Errors are ignored, but if the found files limit
 * is achieved the fact is memorized in result
 */
void search_text(void * arg) {
	file_task_t * res = (file_task_t * )arg;
	pthread_mutex_lock(&res->lock);
	{

		FILE *f = fopen(res->path, "r");
		if (f == NULL) return;
		char line[MAX_LINE];

		fresult_t * presult = NULL;
		int status = OK;
		while (readline(f, line, MAX_LINE) != -1) {
			if (strstr(line, res->to_find) != NULL) {
				if (!enforce_result(&res->search_result, &presult) ) {
					status = TOO_MANY_FOUND_FILES ;
					break;
				}
				presult->count++;
			}
		}
		if (presult != NULL) {
			
			presult->path = strdup(res->path);
			res->search_result.total_ocorrences += presult->count;
		}
		res->search_result.status |= status;
		fclose(f);
		cl_down(res->c_latch);
	}
	pthread_mutex_unlock(&res->lock);
}


/*
 * Search the folder and corresponding sub-folders  
 * where to find files containing the string "to_find".
 * 
 * fills a result with the names and ocurrence count of the files that contains 
 * the text presented in "to_find"
 */
void search(const char * path, const char * to_find,  const char *suffix, search_result_t * res) {	
	char buffer[MAX_PATH];		// auxiliary buffer

	DIR *dir;
    struct dirent *entry;

    if (!(dir = opendir(path)))
        return;
	
    for(int i = 0; (entry = readdir(dir)) != NULL; i++) {
        if (entry->d_type == DT_DIR) {
            if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
                continue;
            snprintf(buffer, MAX_PATH,  "%s/%s", path, entry->d_name);
            search(buffer, to_find, suffix, res);
        } else {
            if (strends(entry->d_name, suffix)) {
			    snprintf(buffer, MAX_PATH, "%s/%s", path, entry->d_name);
			    int n = i % NTHREADS;
				file_task_t * f_task = &tasks[n];
				f_task->path = strdup(buffer);
				f_task->to_find = strdup(to_find);
				
				cl_up(f_task->c_latch);
				thread_pool_submit(&tpool, search_text, f_task);

				res->total_processed++;
			}
        }
    }
    // quando acabarem...
	cl_wait_all(&c_latch);
	

	
    closedir(dir);
}


void show_results( const char *folder, search_result_t * res) {
	
	int n = 0;
	
	//percorrer search results de cada thread e escrever no search result recebido como parametro
	for (int i = 0; i < NTHREADS; ++i) {
		res->status |= tasks[i].search_result.status;
		res->total_results += tasks[i].search_result.total_results;
		res->total_ocorrences += tasks[i].search_result.total_ocorrences;
		
		for (int j = 0; j < tasks[i].search_result.total_results; ++j) {
			res->results[n].path = strdup(tasks[i].search_result.results[j].path);
			res->results[n].count = tasks[i].search_result.results[j].count;
			n++;
		}
		
		pthread_mutex_destroy(&tasks[i].lock);
	}
	
	
	
	int startidx = strlen(folder);
	
	for (int i = 0; i < res->total_results; ++i) {
		fresult_t* fres = res->results + i;
		printf("~%s(%d)\n", fres->path + startidx, fres->count);
	}
	
	printf("\n");
	printf("processed: %d\n", res->total_processed);
	printf("total found files: %d\n", res->total_results);
	printf("total word ocorrences: %d\n", res->total_ocorrences);
	
	if (res->status != OK) printf("warn: the found files limit was achieved\n"); 
}


int main(int argc, char *argv[]) {
	if (argc != 4) {
		printf("usage: traverse <folder> <text> <suffix>\n");
		return 1;
	}
	
	search_result_t result;
	thread_pool_init(&tpool, NTHREADS);
	
	search_result_init(&result, 50000 /* found files limit */);
	
	search(argv[1], argv[2], argv[3],  &result); 
	
	show_results(argv[1], &result);
	
	search_result_destroy(&result);
	 
	return 0;
}

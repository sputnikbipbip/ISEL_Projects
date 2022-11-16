#include <stdio.h>
#include <jansson.h>
#include <curl/curl.h>
#include <errno.h>
#include <string.h>
#include "url_data.h"

struct buffer {
	char *memory;
	size_t size;
};

static size_t createBuffer(char *contents, size_t size_elem, size_t n_elem, void *userp) {
	size_t newSize = size_elem * n_elem;
	struct buffer * bufferptr = (struct buffer *)userp;
	
	char *ptr = realloc(bufferptr->memory, bufferptr->size + newSize + 1);
	if(NULL == ptr) {
		fprintf(stderr,"OutOfMemory,size: %zd", bufferptr->size);
		return -1;
	}
	bufferptr->memory = ptr;
	memcpy(&(bufferptr->memory[bufferptr->size]), contents, newSize);
	bufferptr->size += newSize;
	bufferptr->memory[bufferptr->size] = 0;
	return newSize;
}

json_t *http_get_json_data(const char *url) {
	json_error_t error;
	struct buffer woodStick;
	woodStick.memory = malloc(1);
	woodStick.size = 0;
	curl_global_init(0);
	CURL *curl = curl_easy_init();
	curl_easy_setopt(curl, CURLOPT_URL, url);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, createBuffer);
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, &woodStick);
	curl_easy_setopt(curl, CURLOPT_VERBOSE, 1);
	CURLcode result = curl_easy_perform(curl);
	if (CURLE_OK != result) {
		fprintf(stderr, "lib curl error: %d\n", result);
	}
	curl_easy_cleanup(curl);
	curl_global_cleanup();
	const char* aux = woodStick.memory;
	size_t buflen = woodStick.size;
	size_t flags = 0;
	
	json_t *root = json_loadb(aux, buflen, flags, &error);
	if (!json_is_object(root)) {
		return NULL;
	}
	free(woodStick.memory); 
	return root; 
}

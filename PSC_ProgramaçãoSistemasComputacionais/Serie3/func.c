#include <stdio.h>
#include <stdlib.h>
#include <jansson.h>
#include <string.h>
#include <curl/curl.h>
#include <errno.h>
#include "func.h"

void init_vector(Vector * x) {
	for(int i = 0; i < x->maxSize; i++) {
		x->bodies[i] = NULL;
	}
}

void vector_destroy(Vector * vector) {
	free(vector->bodies);
	free(vector);
	
}

Vector *vector_create(size_t vectorSize) {
	Vector *vector = malloc(sizeof * vector);
	if(NULL == vector)
		return NULL;
	vector->bodies = malloc(vectorSize * sizeof *vector->bodies);
	if(NULL == vector->bodies) {
		free(vector);
		return NULL;
	}
	vector->maxSize = vectorSize;
	vector->currentSize = 0;
	init_vector(vector);
	return vector;
}

void free_all() {
	vector_destroy(v);
}

/********************************************************************solar function**********************************************************************/


int solar_insert_body(Body *body) {
	if(NULL == body) return 0;
	if(v->currentSize >= v->maxSize) return 0;
	size_t aux = v->currentSize;
	v->currentSize = ++aux;
	v->bodies[aux] = body;
	return 1;
}
Body * solar_get_body(const char *body_name) {
	for(int i = 0; i < v->maxSize; ++i) {
		if(NULL != v->bodies[i]) {
			if(strcmp((const char*)v->bodies[i], body_name) == 0)
				return v->bodies[i];
		}
	}
	return NULL;	
}

Vector * solar_get_planets() {
	planets = vector_create(v->maxSize);
	for(int i = 0; i < planets->maxSize; ++i) {
		if(NULL != v->bodies[i])
			if(v->bodies[i]->isPlanet == 1) {
				planets->bodies[i] = v->bodies[i];
				planets->currentSize++;
			}
	}
	return planets;
}

Body * solar_get_satellites(const char * body_name) {
	Body *aux = solar_get_body(body_name);
	if(NULL == aux) return NULL;
	return aux;
}

Body * setBody(json_t *root) {
	Body *body = malloc(sizeof *body);
	if(NULL == body){
		printf("Out of memory!!!\n");
        return NULL;
	}
	if(root == NULL) return NULL;
	strncpy(body->name, json_string_value(json_object_get(root, "englishName")), 15);
	body->radius = json_real_value(json_object_get(root, "meanRadius"));
	json_t * massExtraction = json_object_get(root, "mass");
	body->mass = json_real_value(json_object_get(massExtraction, "massValue"));
	body->gravity = json_real_value(json_object_get(root, "gravity"));
	body->isPlanet = json_boolean_value(json_object_get(root, "isPlanet"));
	json_t * satellitesArray = json_object_get(root, "moons");
	if(json_is_array(satellitesArray)){
		size_t moonsSize = json_array_size(satellitesArray);
		for(size_t i = 0; i < moonsSize; i++) {
			json_t * satellite = json_array_get(satellitesArray, i);
			strncpy(body->satellites[i], json_string_value(json_object_get(satellite, "moon")), 15);
			body->satelliteSize = moonsSize;
		}
	}
	return body;
}

extern Vector * v;
extern Vector * planets;

void init_data(char * url, json_t *root) {
	json_t *array = json_object_get(root, "bodies");
	size_t arraySize = json_array_size(array);
	v = vector_create(arraySize);
	for(size_t index = 0; index < arraySize; ++index, v->currentSize++) {
		json_t * object = json_array_get(array, index);
		Body * body = setBody(object);
		v->bodies[index] = body;
	}
	json_decref(root);
}


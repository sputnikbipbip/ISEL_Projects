#include <stdio.h>
#include <stdlib.h>
#include <jansson.h>
#include <string.h>
#include <curl/curl.h>
#include <errno.h>

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
	body->satellites = NULL;
	if(json_is_array(satellitesArray)){
		size_t moonsSize = json_array_size(satellitesArray);
		body->satellites = malloc(sizeof *body->satellites * moonsSize);
		for(size_t i = 0; i < moonsSize; i++) {
			json_t * satellite = json_array_get(satellitesArray, i);
			body->satellites[i] = (char*)json_string_value(json_object_get(satellite, "moon"));
			//printf("Sattellite name = %s\n\n", body->satellites[i]);
			body->satelliteSize = moonsSize;
		}
	}
	return body;
}

void free_all() {
	vector_destroy(planets);
	vector_destroy(v);
}


void print_vector(Vector * x) {
	int counter = 0;
	while(counter < x->maxSize) {
		if(x->bodies[counter] != NULL) {
			printf("body name = %s\n", x->bodies[counter]->name);
			printf("body radius = %f\n", x->bodies[counter]->radius);
			printf("body mass = %f\n", x->bodies[counter]->mass);
			printf("body gravity = %f\n", x->bodies[counter]->gravity);
			printf("body isPlanet = %i\n\n", x->bodies[counter]->isPlanet);
		}
		counter++;
	}
}

void userInput() {
	int Power = 1;
	int* input;
	while(Power) {
		printf("\n###### Please select any of the options down #######\n");
		printf("1->listar dados de um corpo celeste\n");
		printf("2->listar os corpos que são planetas\n");
		printf("3->listar os satélites de um dado corpo\n");
		printf("4->listar todos os corpos celestes\n");
		printf("5->terminar o programa\n");
		scanf("%i", input);
		switch(*input) {
			case 1:{
				char string[20];
				scanf("%s", string);
				Body * b = solar_get_body(string);
				printf("\nname = %s\n", b->name);
				printf("radius = %f\n", b->radius);
				printf("mass = %f\n", b->mass);
				printf("gravity = %f\n", b->gravity);
				printf("isPlanet = %i\n\n", b->isPlanet);
				break;
			}
			case 2:	{
				Vector * aux = solar_get_planets();
				print_vector(aux);
				break;
			}
			case 3:{
				char string[20];
				scanf("%s", string);
				Body * b = solar_get_satellites(string);
				if(NULL == b){
					printf("\nPlanet inserted doesn't exist in the system\n\n");
					break;
				}
				printf("Number of satellites = %lu\n", b->satelliteSize);
				for(int i = 0; i < b->satelliteSize; i++) {
					printf("%s\n", b->satellites[i]);
				}
				break;
			}
			case 4:
				print_vector(v);
				break;
			case 5:
				printf("System shutting down.......\n");
				Power = 0;
				free_all();
				break;
			default: 
				printf("Your option isn't available, please try aggain.\n");
				break;
		}
	}
}

int main(int argc, char *argv[]) {	
	const char * url = argv[1];
	json_t *root = http_get_json_data(url);
	json_t *array = json_object_get(root, "bodies");
	size_t arraySize = json_array_size(array);
	v = vector_create(arraySize);
	for(size_t index = 0; index < arraySize; ++index) {
		json_t * object = json_array_get(array, index);
		Body * body = setBody(object);
		v->bodies[index] = body;
	}
	json_decref(root);
	userInput();
	return 0;
}

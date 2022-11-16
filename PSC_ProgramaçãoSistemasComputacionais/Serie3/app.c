#include <stdio.h>
#include <string.h>
#include <jansson.h>
#include "url_data.h"
#include "func.h"

void print_vector(Vector * x, int typeOfPrint) {
	int counter = 0;
	if(typeOfPrint) {
		while(counter < x->maxSize) {
			if(x->bodies[counter] != NULL) {
				printf("name = %s\n", x->bodies[counter]->name);
				printf("radius = %f km\n", x->bodies[counter]->radius);
				printf("mass = %f x 10²⁴ kg\n", x->bodies[counter]->mass);
				printf("gravity = %f m/s²\n", x->bodies[counter]->gravity);
				printf("Is a Planet ? ");
				printf(x->bodies[counter]->isPlanet ? "true\n\n" : "false\n\n");
			}
			counter++;
		}
	}else{
		while(counter < x->maxSize) {
			if(x->bodies[counter] != NULL && x->bodies[counter]->isPlanet == 1) {
				printf("name = %s\n", x->bodies[counter]->name);
				printf("radius = %f km\n", x->bodies[counter]->radius);
				printf("mass = %f x 10²⁴ kg\n", x->bodies[counter]->mass);
				printf("gravity = %f m/s²\n", x->bodies[counter]->gravity);
				printf("Is a Planet ? ");
				printf(x->bodies[counter]->isPlanet ? "true\n\n" : "false\n\n");
			}
			counter++;
		}
	}
}

void userInput() {
	int Power = 1;
	int input;
	while(Power) {
		printf("\n###### Please select one of the options down #######\n");
		printf("1->listar dados de um corpo celeste\n");
		printf("2->listar os corpos que são planetas\n");
		printf("3->listar os satélites de um dado corpo\n");
		printf("4->listar todos os corpos celestes\n");
		printf("5->terminar o programa\n");
		scanf("%i", &input);
		switch(input) {
			case 1:{
				char string[20];
				scanf("%s", string);
				Body * b = solar_get_body(string);
				if(NULL == b){
					printf("\nPlanets doesnt exist on the app\n");
					break;
				}
				printf("\nname = %s\n", b->name);
				printf("radius = %f km\n", b->radius);
				printf("mass = %f x 10²⁴ kg\n", b->mass);
				printf("gravity = %f m/s²\n", b->gravity);
				printf("Is a Planet ? ");
				printf(b->isPlanet ? "true\n\n" : "false\n\n");
				break;
			}
			case 2:	{
				Vector * aux = solar_get_planets();
				print_vector(aux, 0);
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
				print_vector(v, 1);
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

extern Vector * v;
extern Vector * planets;

int main(int argc, char *argv[]) {	
	json_t *root = http_get_json_data(argv[1]);
	init_data(argv[1], root);
	userInput();
	return 0;
}

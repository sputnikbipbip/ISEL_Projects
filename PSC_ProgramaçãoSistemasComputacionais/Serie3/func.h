#ifndef FUNC_H
#define FUNC_H

typedef struct Body {
	char name [15];
	char satellites [90][15];
	size_t satelliteSize;
	int isPlanet;
	double radius;
	double mass;
	double gravity;
} Body;

typedef struct Vector {
	Body ** bodies;
	size_t maxSize;
	size_t currentSize;
} Vector;

Vector * v;
Vector * planets;

void init_data(char * url, json_t *root);
void free_all();
Vector * solar_get_planets();
Body * solar_get_satellites(const char * body_name);
Body * solar_get_body(const char *body_name);

#endif

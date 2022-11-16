all: solar 

url_data.o: url_data.c
	gcc -c -g url_data.c

func.o: func.c
	gcc -c -g func.c 	

app.o: app.c 
	gcc -c -g app.c 

solar: app.o func.o url_data.o 
	gcc app.o func.o url_data.o -l jansson -l curl -o solar 

clean:
	rm *.o

all: search.o thread_pool.o count_latch.o queue.o
	gcc -Wall -pedantic -o search -g search.o thread_pool.o count_latch.o queue.o -lpthread
	
search.o: search.c thread_pool.o count_latch.o
	gcc -Wall -c -g search.c thread_pool.o count_latch.o
	
thread_pool.o: thread_pool.c queue.o
	gcc -Wall -c -g thread_pool.c queue.o
	
count_latch.o: count_latch.c
	gcc -Wall -c -g count_latch.c 

queue.o: queue.c
	gcc -Wall -c -g queue.c
		
clean:
	rm -rf *.o search

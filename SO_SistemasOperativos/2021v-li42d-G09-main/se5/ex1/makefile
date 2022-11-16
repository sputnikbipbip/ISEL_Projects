all: test_semaphore.o semaphore.o
	gcc -Wall -o test -g test_semaphore.o semaphore.o -lpthread
	
test_semaphore.o: test_semaphore.c
	gcc -Wall -c -g test_semaphore.c
	
semaphore.o: semaphore.c
	gcc -Wall -c -g semaphore.c 
		
clean:
	rm -rf *.o *.so *.a test

all: test_count_latch.o count_latch.o
	gcc -Wall -o test -g test_count_latch.o count_latch.o -lpthread
	
test_count_latch.o: test_count_latch.c
	gcc -Wall -c -g test_count_latch.c
	
count_latch.o: count_latch.c
	gcc -Wall -c -g count_latch.c 
		
clean:
	rm -rf *.o *.so *.a test

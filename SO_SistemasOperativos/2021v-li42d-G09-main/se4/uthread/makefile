all: libuthread.so libuthread.a

libuthread.so: uthread.o event.o semaphore.o barrier.o ctxswitch.o
	gcc -o libuthread.so -shared uthread.o event.o semaphore.o barrier.o ctxswitch.o

libuthread.a: uthread_nogot.o event_nogot.o semaphore_nogot.o barrier_nogot.o ctxswitch.o 
	ar rcv libuthread.a uthread_nogot.o event_nogot.o semaphore_nogot.o barrier_nogot.o ctxswitch.o

#
# uthread core
#

uthread.o: uthread.c
	gcc -c -g -fPIC -Wall -I ../include uthread.c
	
uthread_nogot.o : uthread.c
	gcc -c -o uthread_nogot.o -g -Wall -I ../include uthread.c
	
ctxswitch.o: ctxswitch.s
	as -o ctxswitch.o -c ctxswitch.s

#
# uthread syncronizers
#

barrier.o: barrier.c
	gcc -c -g -fPIC -Wall -I ../include barrier.c 
	
event.o: event.c
	gcc -c -g -fPIC -Wall -I ../include event.c 

semaphore.o: semaphore.c
	gcc -c -g -fPIC -Wall -I ../include semaphore.c 
	
barrier_nogot.o: barrier.c
	gcc -c -g -o barrier_nogot.o -Wall -I ../include barrier.c 

event_nogot.o: event.c
	gcc -c -g -o event_nogot.o -Wall -I ../include event.c 
	
semaphore_nogot.o: semaphore.c
	gcc -c -g -o semaphore_nogot.o -Wall -I ../include semaphore.c 
	
clean:
	rm -rf *.o *.so *.a

install:
	sudo cp libuthread.a libuthread.so /usr/lib

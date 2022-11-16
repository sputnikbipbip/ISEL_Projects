dynamic: simpletest.c event_test.c thread_state_test.c thread_priority_test.c barriers_test.c
	gcc -o simpletest -Wall -g -I ../include simpletest.c /usr/lib/libuthread.so
	gcc -o event_test -Wall -g -I ../include event_test.c /usr/lib/libuthread.so
	gcc -o thread_state_test -Wall -g -I ../include thread_state_test.c /usr/lib/libuthread.so
	gcc -o thread_priority_test -Wall -g -I ../include thread_priority_test.c /usr/lib/libuthread.so
	gcc -o barriers_test -Wall -g -I ../include barriers_test.c /usr/lib/libuthread.so


static : simpletest.c event_test.c thread_state_test.c barriers_test.c
	gcc -o simpletest -Wall -g -I ../include simpletest.c /usr/lib/libuthread.a
	gcc -o event_test -Wall -g -I ../include event_test.c /usr/lib/libuthread.a
	gcc -o thread_state_test -Wall -g -I ../include thread_state_test.c /usr/lib/libuthread.a
	gcc -o thread_priority_test -Wall -g -I ../include thread_priority_test.c /usr/lib/libuthread.a
	gcc -o barriers_test -Wall -g -I ../include barriers_test.c /usr/lib/libuthread.a

clean:
	rm -rf simpletest event_test thread_state_test thread_priority_test barriers_test

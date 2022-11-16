prog.o: prog.c lib1.so lib2.so
	gcc -Wall prog.c -o prog -ldl

lib1.so: lib1.o
	gcc -shared lib1.o -o lib1.so
	
lib2.so: lib2.o
	gcc -shared lib2.o -o lib2.so

lib1.o: lib1.c
	gcc -mcmodel=large -fPIC -c lib1.c -o lib1.o
	
lib2.o: lib2.c
	gcc -mcmodel=large -fPIC -c lib2.c -o lib2.o
		
clean:
	rm -rf *.o *.so *.a prog

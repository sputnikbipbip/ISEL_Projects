#include <stdio.h>
#include <stdint.h>


const char src[20] = " Leonor, my darling.";
char dest[5] = "Hello";

char* strncat_one(char *dest, const char *src, size_t n);

int main(){
	size_t n = 15;
	const char* a = src;
	char* b = dest;	
	strncat_one(b, a, n);
	puts(dest);
	return 0;
}

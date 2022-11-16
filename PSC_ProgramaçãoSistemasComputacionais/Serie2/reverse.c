#include <stdio.h>
#include <stdint.h>

uint64_t result;

uint64_t reverse(uint64_t val); 

int main(){
	result = reverse(4294967296);
	printf("reversed = %lx \n", result);
	result = reverse(3);
	printf("reversed = %lx \n", result);
	result = reverse(8);
	printf("reversed = %lx \n", result);
	return 1;
}
	

#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <ctype.h>
#include <stdlib.h>
#include "../include/bmp.h"


/*Se o ficheiro não tiver compressão (BI_RGB), se usar 24 bits por pixel, 
 * se tiver uma largura de valor múltiplo de 4 e se tiver sido passado ao 
 * programa um segundo argumento, com um valor numérico, positivo ou negativo, 
 * mas diferente de zero, adicione esse valor a todos os componentes de cor de cada 
 * pixel, tendo o cuidado de garantir, nessas somas, um limite inferior de 0 e superior de 255.*/
 
uint8_t checkLimit(uint8_t colour, const int8_t value){
	if((value + colour) < 0) return 0;
	if((value + colour) > 255) return 255;
	printf("Colour = %d\n", colour);
	printf("value = %u\n", value);
	return colour + value;
}
 

int main(int argc, char * argv[]) {
	if (argc < 2 || argc > 3 || strlen(argv[1])  < 5 ||
		strcmp(argv[1] + (strlen(argv[1]) - 4), ".bmp") != 0)
	{
		fprintf(stderr, "use: %s {filename}.bmp [offset]\n", argv[0]);
		return 1;
	}
	int fd = open(argv[1], O_RDWR);
	if (fd == -1) {
		perror("Failed to open file");
		return 2;
	}
	
	
	struct stat statbuf;
	if (fstat(fd, &statbuf) == -1) {  // MIGHT FAIL if ftruncate has not been called yet
		perror(">> Failed to get the size of shared memory");
		close(fd);
		return 2;
	}
	size_t size = statbuf.st_size;
	
	void * ptr = mmap(NULL, size, PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
	close(fd);
	
    BITMAPFILEHEADER * bitMapFile = (BITMAPFILEHEADER *)ptr; 
    char firstLetter = bitMapFile->bfType & 0xFF;
    char secondLetter = bitMapFile->bfType >> 8;
    if('B' != firstLetter || 'M' != secondLetter)
    {
		fprintf(stderr, "Incorrect file type -> %c%c", firstLetter, secondLetter);
	} else {
		printf("\nFile type is correct :%c%c\n", firstLetter, secondLetter);
	}
	printf("bfType: %u\n", bitMapFile->bfType);
	printf("bfSize: %u\n", bitMapFile->bfSize);
	//bfOffBits -> onde começa os pixels
	printf("bfOffBits: %u\n", bitMapFile->bfOffBits);
	
	//iterate to the next struct
	void *bitMapPtr = ptr + 14;
	
	BITMAPINFOHEADER *bitMapInfo = (BITMAPINFOHEADER *) bitMapPtr;
	printf("biWidth: %d\n", bitMapInfo->biWidth);
	printf("biHeight: %d\n", bitMapInfo->biHeight);	
	printf("biBitCount: %u\n", bitMapInfo->biBitCount);	
	printf("biCompression: %u\n", bitMapInfo->biCompression);
	
	
	if(bitMapInfo->biCompression == 0 && bitMapInfo->biBitCount == 24 && bitMapInfo->biWidth % 4 == 0 
		&& argc == 3 && atoi(argv[2]) !=  0){

			RGBTRIPLE *pixels = (RGBTRIPLE *)(ptr + bitMapFile->bfOffBits);

			int numPixels = bitMapInfo->biWidth * bitMapInfo->biHeight;
			
			const int8_t aux = (int8_t) atoi(argv[2]);
			printf("Constant value %d\n", aux);
			
			
			for(int i = 0; i < numPixels; i++, pixels += 1){
				printf("RED _> %d\n", pixels->rgbtRed);
				pixels->rgbtRed = checkLimit(pixels->rgbtRed, aux);
				printf("RED (after) _> %d\n", pixels->rgbtRed);
				printf("GREEN _> %d\n", pixels->rgbtGreen);
				pixels->rgbtGreen = checkLimit(pixels->rgbtGreen, aux);
				printf("GREEN _> (after) %d\n", pixels->rgbtGreen);
				printf("BLUE _> %d\n", pixels->rgbtBlue);
				pixels->rgbtBlue = checkLimit(pixels->rgbtBlue, aux);	
				printf("BLUE _> (after) %d\n", pixels->rgbtBlue);
				
			}
	} else {
		perror("Cannot support given image");
		return 3;
	}
	return 0;
}

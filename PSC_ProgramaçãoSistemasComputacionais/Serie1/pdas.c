#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <stdlib.h>



typedef struct {
    uint8_t byte_count;
    uint16_t address;
    uint8_t type;
    uint8_t data[256];
    uint8_t checksum;
} IntelHex;

//convert char to integer
int ctoi(char c){
    int result;
    if(c > 64 && c < 71){
        return result = c - '7';
    }else if(c > 47 && c < 58){
        return result = c - '0';
    }else{
        fprintf(stderr, "%s \n","Value is not hexadecimal.\n" );
        exit(1);
    }
}

int decode_record(char *record, IntelHex *pblock){
    if(record == NULL || *record != ':'){
        return 1;
    }
    int len = strlen(record);

    pblock->byte_count = ((ctoi(record[1]) << 4) + ctoi(record[2]));

    pblock->address = (ctoi(record[3]) << 12) + (ctoi(record[4]) << 8) + (ctoi(record[5]) << 4) +
                      ctoi(record[6]);
	
    pblock->type = (ctoi(record[7]) << 4) + ctoi(record[8]);

    for(int s = 0, i = 9, j = 10; j < len-2 || i < len-3; s += 2, i = i+2, j = j+2){
        pblock->data[s] = ctoi(record[i]);
        pblock->data[s+1] = ctoi(record[j]);
    }
    pblock->checksum = ((ctoi(record[len-2])<<4) +ctoi(record[len-1]));
    return 0;
}

size_t read_line(FILE *fd, char *buffer, size_t buffer_size){
    if(fd == NULL){
        fprintf(stderr, "%s \n","The pointer to the file is NULL.\n" );
        exit(1);
    }
    char c = fgetc(fd);
    if(c == EOF){
		fprintf(stderr, "%s \n","End of file found.\n" );
        return -1;
    }
    size_t i = 0;
    while(c != '\n' && i < buffer_size && c != '\0'){
        buffer[i++] = c;
        c = fgetc(fd);		
    }
    buffer[i] = '\0';
    fclose(fd);
    return i;
}
char line[100];

#define ARRAY_SIZE(a) (sizeof(a)/sizeof(a)[0])

int main(int argc, char *argv[]){
	IntelHex block;
	int counter = 0;
	int size;
	FILE
		*fptr;
	fptr = fopen(argv[1], "r");
	read_line(fptr++, line, ARRAY_SIZE(line));
	if(!(decode_record(line,&block))){
		int imm8;
		int rd;
		int rn;
		int rm;
		int rs;
		int imm4;
		int imm10;
		for(int i = 2, pc = 0; i < block.byte_count*2; i+=4, pc+=2){
			printf("%04x  %x%x%x%x     ", pc, block.data[i-2], block.data[i-1], block.data[i], block.data[i+1]);
			switch(block.data[i]){	
				case 6:
					//mov rd, <imm8>
					imm8 = ((block.data[i+1]<<4) + block.data[i-2]);
					rd = block.data[i-1];
					printf("mov     r%d, %d \n", rd, imm8);
					break;
				case 10:
					//sub rd, rn, <imm4>
					if((block.data[i+1]&8) == 8){
						rd = block.data[i-1];	
						rn = (block.data[i-2]&7);
						imm4 = ((block.data[i+1] && 7) + ((block.data[i-2] && 8)>>3));
						printf("sub     r%d, r%d, %d   \n", rd, rn, imm4);
					//add rd, rn, <imm4>	
					}else{
						imm4 = block.data[i+1]>>1;
						rn = block.data[i-2];
						rd = block.data[i-1];
						printf("add     r%d, r%d, %d   \n", rd, rn, imm4);
					}	
					break;
				case 4:
					//bzc
					if((block.data[i+1]&4) == 4){
						imm10 = (((block.data[i+1] << 8)) + ((block.data[i-2]<<4)) +  block.data[i-1]);
						printf("bzc     0x%04x\n", imm10);
					//bzs	
					}else{
						imm10 = (((block.data[i+1] << 8)) + ((block.data[i-2]<<4)) +  block.data[i-1]);
						printf("bzs     0x%04x\n", imm10);
						printf("i     0x%04x\n", block.data[i]);
						printf("i+1   0x%04x\n", block.data[i+1]);
						printf("i-2   0x%04x\n", block.data[i-2]);
						printf("i-1   0x%04x\n", block.data[i-1]);
					}	
					break;
				case 8:
					// add rd, rn, rm
					rm = (block.data[i+1]&7<<1) + ((block.data[i-2]>>3)&1);
					rn = block.data[i-2]&7;
					rd = block.data[i-1]; 
					printf("add     r%d, r%d, r%d   \n", rd, rn, rm);
					break;
				case 11:
					//mov rs, rs
					rs = ((block.data[i+1] && 7)<<1 + ((block.data[i-2] && 8)>>3));;
					rd = block.data[i-1];
					if(rd == 15){
						puts("mov     pc, lr");
						break;
					}
					printf("mov     r%d, r%d \n", rd, rs);
					break;						
			}
		}		
	}
	return 1; 	
}

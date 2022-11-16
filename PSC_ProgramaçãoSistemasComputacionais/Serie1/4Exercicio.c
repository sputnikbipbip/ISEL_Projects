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
        return 0;
    }
    int len = strlen(record);

    pblock->byte_count = (ctoi(record[1]) << 4) + ctoi(record[2]);

    pblock->address = (ctoi(record[3]) << 12) + (ctoi(record[4]) << 8) + (ctoi(record[5]) << 4) +
                      ctoi(record[6]);

    pblock->type = (ctoi(record[7]) << 4) + ctoi(record[8]);

    for(int s = 0, i = 9, j = 10; j < len-2 || i < len-3; ++s, i = i+2, j = j+2){
        pblock->data[s] = ((ctoi(record[i])<<4) + ctoi(record[j]));
        printf("data[%d] = %d\n",s,pblock->data[s]);
    }
    pblock->checksum = ((ctoi(record[len-2])<<4) +ctoi(record[len-1]));
    printf("checksum = %d\n",pblock->checksum);
    return 1;
}

int main(){
    IntelHex block;
    decode_record(":10041a000e24c00c0008b10c11080c5c910c1020c1", &block);
    printf(":%02x %04x %02x ", block.byte_count, block.address, block.type);
    for (int i = 0; i < block.byte_count; i++)
        printf("%02x", block.data[i]);
    printf(" %02x\n", block.checksum);
    return 1;
}


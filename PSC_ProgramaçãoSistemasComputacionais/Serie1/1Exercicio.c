#include <stdio.h>
#include <stdint.h>
#include <math.h>


uint32_t get_bits(uint32_t value, int msp, int lsp){
    value <<= 32-msp;
    value >>= (32-msp) + lsp;
    return value;
}

uint32_t setBits(uint32_t value, int msp, int lsp, uint32_t new_value){
    //backup - save bits at right of lsp
    uint32_t backup = value;
    backup <<= 32-lsp;
    backup >>= 32-lsp;
    value >>= msp;
    value <<= (msp-lsp);
    value += new_value;
    value <<= lsp;
    value += backup;
    return value;

}

int main(){
    uint32_t value = 0x2AD555BC;
    uint32_t valueFunction1 = get_bits(value,6, 3);
    printf("\nThe value between the two positions is : %d \n", valueFunction1);

    uint32_t valueFunction2 = setBits(value, 11, 8, 2);
    printf("value = %p \n", valueFunction2);
    return 0;
}







/* Implementação Anterior



uint32_t setBits(uint32_t value, int msp, int lsp, uint32_t new_value){
    //save the right bits of the value
    int mask = 1;
    int power = 0;
    int counter = 0;
    int backup = 0;
    for(; counter <lsp; ++counter){
        if(mask&value)
            backup += pow(2,power);
        value >>= 1;
        ++power;
    }
    //set bits with the new value
    //and increment it in the backup variable
    while(lsp <= msp){
        if(!(value&mask) && (new_value&mask)){
            value|1;
        }else if((value&mask) && !(new_value&mask)){
            value&0;
        }
        if(new_value&mask){
            backup += pow(2,power);
        }
        value >>= 1;
        new_value >>= 1;
        ++counter, ++lsp, ++power;
    }
    //puts the value in its original position and adds the backup value
    value <<= counter;
    value += backup;
    return value;

}
*/

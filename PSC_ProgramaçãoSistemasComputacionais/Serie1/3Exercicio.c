#include <stdio.h>
#include <stdint.h>

typedef union{
    int integer;
    float real;
    struct{
        unsigned int mantissa: 23;
        unsigned int exponent: 8;
        unsigned int signal: 1;
    };
} Float;

int counter(int c){
    int result = 0;
    while(c > 0){
        c >>= 1;
        result++;
    }
    return result;
}

float string_to_float(char *string){
    Float v;
    int withoutIntegerPart = 0;
    uint16_t integer = 0;
    uint16_t decimal = 0;
    uint16_t exponent = 127;
    if(*string == '-'){
        v.signal = 1;
        *string++;
    }
    while(*string != '.'){
        int digit = (*string++ - '0');
        integer = integer * 10 + digit;
    }
    string++;
    int fraction = 1;
    while(*string != 0){
        int digit = (*string++ - '0');
        decimal = decimal * 10 + digit;
        fraction *= 10;
        ++withoutIntegerPart;
    }
    int same = 1 * fraction;             //same is for 0,4 * 2 = same (but is proportional to fraction)
    uint32_t mantissa = integer;
    if(decimal*2 == same){
        mantissa <<= 1;
        mantissa += 1;
        decimal = same;
    }
    while(decimal != same){
        if(decimal*2 > same){
            decimal = (decimal*2)%same;
            mantissa += 1;
            mantissa <<= 1;
        }else if(decimal*2 < same){
            decimal *= 2;
            mantissa <<= 1;
        }else if(decimal*2 == same){
            mantissa <<= 1;
            mantissa += 1;
            decimal = same;
        }
    }
    //exponent
    int lessOrBigger = counter(integer);
    if(lessOrBigger == 0){
        exponent -= withoutIntegerPart;
    }else{
        exponent += lessOrBigger-1;
    }
    //mantissa shifter
    int nbitsMantissa = counter(mantissa);
    mantissa <<= 23 - nbitsMantissa;
    //take off most significant
    int mask = 4194303;
    int auxMantissa = mantissa;
    //not possible with uint32_t, don't know why
    auxMantissa &= mask;
    mantissa = auxMantissa;
    mantissa <<= 1;
    v.mantissa = mantissa;
    v.exponent = exponent;
    return v.real;
}

int main() {
    float a = string_to_float("-65415.5");
    float b = string_to_float("323.5");
    float c = string_to_float("0.125");
    printf("%f\n%f\n%f\n", a, b, c);
    return 0;
}


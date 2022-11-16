/*
#1 arg - RDI (*dest[]) 1 byte
#2 arg - RSI (*src[]) 1 byte
#3 arg - RDX (3) 2 bytes
*/


	.text
	.global strncat_one
strncat_one:
	mov $0, %rax              /*retorno, para percorrer array dest*/
	mov $0, %r9
	mov $0, %rcx             
	cmp %rdx, %rax            /*se size_t for 0 não faz nada*/
	jz end
first:                        /*percorre o array até ser == /0 */
	mov (%rdi, %rcx, 1) , %r8	  /*dest[]*/
	inc %rcx
	cmp %rax, %r8
	jnz first
	dec %rcx
second:
    cmp %rax, %rdx            /*rax == size_t*/
	je end	
	inc %rax                  /*serve para detetar se atingimos o limite*/
	mov (%rsi, %r9), %r8      /*src[]*/
	mov %r8, (%rdi, %rcx) 	  /*dest[]*/
	inc %rcx
	inc %r9
	jmp second
end:
	mov $0, %r9
	mov %r9, (%rdi, %rcx) 
	mov %rdi, %rax             /*retorna o array dest*/
	ret
	

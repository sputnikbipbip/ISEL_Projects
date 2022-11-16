
	.text
	.global reverse
reverse:
	mov $1, %rbx 
	mov $0, %rcx    
for:
	test %rdi, %rbx
	jz dontAddBit
	add $1, %rdx
dontAddBit:
	cmp $63, %rcx
	je forEnd
	inc %rcx
	shl $1, %rdx
	shr $1, %rdi
	jmp for	
forEnd:	
	mov %rdx, %rax
	ret

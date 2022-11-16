
		.text
		.global	memswap
memswap:
    push   %rbx
    push   %rbp
    push   %r12 
	mov 	%rdi,	%rbx	#void*one
	mov		%rsi,	%rbp	#void*other
	mov 	%rdx, 	%r12	#size_t width	
	add		$7,		%r12
	shr		$3,		%r12		
	shl		$3,		%r12	
	sub 	%r12,	%rsp
	mov 	%rsp,	%rdi
	mov		%rbx,	%rsi
	mov		%r12, 	%rdx
	call 	memcpy
	mov 	%rbx,	%rdi
	mov		%rbp,	%rsi
	mov		%r12, 	%rdx
	call 	memcpy
	mov 	%rbp,	%rdi
	mov		%rsp,	%rsi
	mov		%r12, 	%rdx
	call 	memcpy
	add		%r12, 	%rsp
	pop		%r12
	pop		%rbp
	pop		%rbx
	ret

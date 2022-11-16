
		.text
		.global	quickSort
quickSort:
	push	%rbx			#last
	push 	%rbp			#right
	push 	%r12   			#left
	push 	%r13			#base
	push 	%r14			#nel
	push 	%r15			#width
	mov 	%rdi,	 %r13	
	mov 	%rsi,	 %r14
	mov 	%rdx,	 %r15
	sub		$8, 	 %rsp
	mov 	%rcx, 	 (%rsp) 
	mov 	%r15,	 %rax             /*rax -> registo auxiliar da multiplicação*/
	mov 	%r14,    %rcx             /*podia ter sido feito -> lea -1(%r14), %rcx*/
	dec		%rcx
	mul 	%rcx
	lea		(%r13,	 %rax),  %rbx	   /* void *last = base + width * (nel - 1), == add %r13, %rax, %rbx */
	mov 	%rbx, 	 %rbp			   /* void *right = last*/
	lea 	(%r13, 	 %r15),	 %r12      /*void *left = base + width*/
while_loop1:
	cmp 	%r12,	 %rbp              #left <= right
	jb		while_loop2
	mov 	%r12,	 %rdi
	mov 	%r13, 	 %rsi
	call  	*(%rsp)
	cmp 	$0, 	 %eax          	   #32bits ((*compar)(left, base) <= 0) - tenho que ter em consideração que estou a comparar 32 bits
	jg   	while_loop2       	       #ter em consideração o sinal less or equal    (0 / 1 / -1)
	lea		(%r12,	 %r15),	 %r12
	jmp while_loop1
while_loop2:
	cmp 	%rbp,	 %r12              #right >= left  
	ja   	if
	mov 	%rbp,	 %rdi
	mov		%r13,	 %rsi
	call  	*(%rsp)
	cmp		$0, 	 %eax			   #(*compar)(right, base) >= 0
	jb  	if
	sub		%r15,	 %rbp              #right -= width
	jmp		while_loop2
if:
	cmp		%rbp,	 %r12			   #(right < left)
	ja 		while_out
	mov 	%r12,	 %rdi
	mov 	%rbp, 	 %rsi
	mov 	%r15,	 %rdx
	call	memswap
	jmp while_loop1
while_out:						       #memswap(base, right, width)
	mov 	%r13, 	 %rdi
	mov 	%rbp,	 %rsi
	mov 	%r15,	 %rdx
	call	memswap
if2:
	cmp 	%rbp,	 %r13             #(right > base)
	jae     if3                       #base >=
	mov 	%r13, 	 %rdi             #1º parametro (base)
	
	mov     %rbp,	 %rax
	sub 	%r13, 	 %rax			  #(right - base)  
	mov		$0,		 %rdx
	div		%r15			          #(right - base) / width
	mov 	%rax, 	 %rsi		      #2º parâmetro    
	
	mov		%r15,	 %rdx			  #3º parametro (width)
	mov		(%rsp),	 %rcx             #4º parametro (compar)
	call	quickSort
if3:
	cmp		%rbp, %rbx  	          #(right < last)
	jbe		end
	lea		(%r15, %rbp), %r12	      #width + right     --  r12 era left, mas já não preciso dele
	mov 	%r12, 	 %rdi		      #1º parametro
	
	sub 	%rbp,	 %rbx			  #(last - right)
	mov		%rbx,	 %rax
	mov		$0,		 %rbx
	div		%r15			          #(last - right) / width
	mov		%rax,	 %rsi		      #2º parametro (last - right) / width
	mov 	%r15,    %rdx		      #3º parametro
	mov		(%rsp),	 %rcx             #4º parametro (compar)
	call 	quickSort
end:
	add		$8, 	 %rsp
	pop		%r15
	pop		%r14
	pop		%r13
	pop		%r12
	pop		%rbp
	pop		%rbx
	ret

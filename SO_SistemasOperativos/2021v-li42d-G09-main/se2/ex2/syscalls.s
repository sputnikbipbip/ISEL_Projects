	.text
.global xopen
.global xgetpid	
.global xwrite
.global xclose

xwrite:
	movq	$1, %rax # 1 -> write
	syscall
	ret

xopen:
	movq	$2, %rax # 2 -> open
	syscall
	ret

xclose:
	movq	$3, %rax # 3 -> close
    syscall
	ret

xgetpid:
	movq	$39, %rax # 39 -> getpid
	syscall
	ret	

.end


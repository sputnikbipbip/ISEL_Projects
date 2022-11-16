
		.text	
	multiply:
		mov r2, 0
		add r1, r1, 0
		bzs while_end
	while:
		add r2, r2, r1
		sub r1, r1, 1
		bzc while
	while_end:
		mov r0, r2
		mov pc, lr

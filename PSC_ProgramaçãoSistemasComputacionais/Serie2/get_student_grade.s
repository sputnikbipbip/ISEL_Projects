/*
typedef struct student { 
int number;
char *name;
short grades[4];
} 
Student;

typedef struct { 
int classId;
int length;
Student *students;
} 
Class;

short get_student_grade(Class *class, int number, int grade_idx) {
						rdi 64bits    esi 32bits  edx 32bits
	short grade = -1;
		cx 16bits
		
	if (grade_idx < 4)
		for (int i = 0; i < class->length; i++)
			if (class->students[i].number == number) {
				grade = class->students[i].grades[grade_idx];
				break;
			}
	return grade;
	
	
	
	posição de class->length -> 8 bytes +  
}
*/
	/* Student */
	.equ number, 0
	.equ name, 8
	.equ grades, 16
	
	/* Class */
	.equ classId, 0
	.equ length, 4
	.equ students, 8

	.text
	.global get_student_grade
get_student_grade:
	mov $-1, %cx
if:
	cmp $4, %edx      /*se for 4 vai para o final*/
	jz end				
	mov $0, %r8d       /*32bits r8d = int i*/
for:
	cmp length(%rdi), %r8d   /* só posso usar 1,4 ou 8*/
	jge end
if_2:	
	mov students(%rdi), %r10
	mov %r8, %r9
	shl $3, %r9
	mov %r8, %rax
	shl $4, %rax
    add %rax, %r9  
	cmp number(%r10, %r9), %rsi
	jne end_if    
	mov grades(%r10, %rdx, 2), %cx   /*2 -> por ser short*/
	jmp end
end_if:
	inc %r8d
	jmp for
end:
	mov %rcx, %rax
	ret
	
	
	

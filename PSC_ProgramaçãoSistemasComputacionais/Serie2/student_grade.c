#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct student {
int number;
char *name;
short grades[4];
} Student; 

Student students[] = {
		{1, "Daniel", {16,13,12,11}},
		{2, "Mariana", {10,14,11,11}},
		{3, "Carlos", {10,6,12,5}},
		{4, "Luís", {12,13,12,10}}
};

typedef struct{
int classId;
int length;
Student *students;
} Class; 
/*
short get_student_grade(Class *class, int number, int grade_idx){
		short grade = -1;
		if(grade_idx < 4)
			for(int i = 0; i < class->length; i++)
				if(class->students[i].number == number){
						grade = class->students[i].grades[grade_idx];
						break;
				}
		return grade;
}
*/

short get_student_grade(Class *class, int number, int grade_idx);

int main(){
	Class class1;
	Student s1, s2, s3, s4;
	s1 = students[0];
	s2 = students[1];
	s3 = students[2];
	s4 = students[3];
	class1.classId = 100;
	class1.length = 3;
	class1.students = students;
	printf("\n\nThe value returned is %d", get_student_grade(&class1, 2, 0));
	printf("\nThe value returned is %d", get_student_grade(&class1, 2, 1));
	printf("\nThe value returned is %d\n\n", get_student_grade(&class1, 2, 2));
	return 0;
}

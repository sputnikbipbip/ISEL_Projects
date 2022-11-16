#include <stdio.h>
#include <string.h>

typedef struct Numbers {
	char array[20];
}Numbers;

Numbers numbers[] = {16, 13, 12, 11};

int comparator(const void *p, const void *q) {
	int l = *((int*)p);
	int r = *((int*)q);
	if(l > r)
		return 1;
	else if (l < r)
		return -1;
	else
		return 0;
}
/*
void memswap(void *one, void *other, size_t width) {
	char tmp[width];
	memcpy(tmp, one, width);
	memcpy(one, other, width);
	memcpy(other, tmp, width);
}
*/
/*
void quickSort(void *base, size_t nel, size_t width, int (*compar)(const void *, const void *)){
	void *last = base + width * (nel - 1), *right = last;
	void *left = base + width;
	do {
		while (left <= right && (*compar)(left, base) <= 0)
			left += width;
		while (right >= left && (*compar)(right, base) >= 0)
			right -= width;
		if (right < left)
			break;
		memswap(left, right, width);
	} while (1);
	memswap(base, right, width);
	if (right > base)
		quickSort(base, (right - base) / width, width, compar);
	if (right < last)
		quickSort(right + width, (last - right) / width, width, compar);
}
*/
void * memcpy(void * destination, const void * source, size_t num);

void memswap(void *one, void *other, size_t width);

void quickSort(void *base, size_t nel, size_t width, int (*compar)(const void *, const void *));

int main() {
	Numbers num;
	num = numbers[0];
	size_t n = sizeof(num.array)/sizeof(num.array[0]);
	quickSort(num.array, n, sizeof(int), comparator);
	printf("Sorted array: n");
	for(int i = 0; i<n; ++i)
		printf("\n arr[%d] = %d", i, num.array[i]);
	puts("\n");
	return 0;
}

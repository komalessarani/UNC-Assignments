/* Example: matrices represented by 2-dimensional arrays */

#include <stdio.h>

int main()
{
  int A[3][3];    // matrix A     
  int B[3][3];    // matrix B
  int C[3][3];    // matrix to store their sum

  // get the values for matrix A
  printf("Please enter 9 values for matrix A:\n");
  for(int i = 0; i < 3; i++){
     for(int j = 0; j < 3; j++){
     	scanf("%d", &A[i][j]);
     }
  }

  // get the values for matrix B
  printf("Please enter 9 values for matrix B:\n");
  for(int i = 0; i < 3; i++){
     for(int j = 0; j < 3; j++){
	scanf("%d", &B[i][j]);
     }
  }

  // sum the values from A and B and store them into matrix C
  printf("C = B + A =\n");
  for(int i = 0; i < 3; i++){
     for(int j = 0; j < 3; j++){
	C[i][j] = B[i][j] + A[i][j];
    	printf("%10i", C[i][j]);
    }
     printf("\n");
  }
}

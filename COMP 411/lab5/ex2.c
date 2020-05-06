#include <stdio.h>

int A[10][10];
int B[10][10];
int C[10][10];

int main() {

int m = 0;
scanf("%d", &m);	//get the value of M

//get the values in matrix A
for(int a = 0; a < m; a++){
  for(int b = 0; b < m; b++){
    scanf("%d", &A[a][b]);
  }
}

//get values in matrix B
for(int c = 0; c < m; c++){
  for(int d = 0; d < m; d++){
    scanf("%d", &B[c][d]);
  }
}

//matrix multiplication
for(int i = 0; i < m; i++){
  for(int j = 0; j < m; j++){
   C[i][j] = 0;
   for(int k = 0; k < m; k++){
      C[i][j] += A[i][k]*B[k][j];
   }
  }
}

//print the matrix
for(int p = 0; p < m; p++){
  for(int r = 0; r < m; r++){
     printf("%6d", C[p][r]);
  }
   printf("\n");
}
}

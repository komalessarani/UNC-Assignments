#include <stdio.h>

int AA[100];  		// linearized version of A[10][10]
int BB[100];  		// linearized version of B[10][10]
int CC[100];  		// linearized version of C[10][10]
int m;       		// actual size of the above matrices is mxm, where m is at most 10

int main() {
   scanf("%d", &m);	//get the value of m

   //get the values in matrix A
   for(int a = 0; a < m; a++){
    for(int b = 0; b < m; b++){
      scanf("%d", &AA[a*m+b]);
    }
   }
  
   //get the values in matrix B
   for(int b = 0; b < m; b++){
     for(int c = 0; c < m; c++){
       scanf("%d", &BB[b*m+c]);
     }
   }

  //matrix multiplication
for(int i = 0; i < m; i++){
   for(int j = 0; j < m; j++){
   CC[i*m+j] = 0;
      for(int k = 0; k < m; k++){
        CC[(i*m)+j] += AA[(i*m)+k] * BB[(k*m)+j];
      }
   }
}

//print the matrix
//for(int c = 1; c <= m*m; c++){
  //   printf("%d ", CC[c]);
    // if(c % m == 0){
//	printf("\n");
  //   }
 // }
//print the matrix
for(int p = 0; p < m; p++){
  for(int r = 0; r < m; r++){
     printf("%d ", CC[p*m+r]);
  }
    printf("\n");
}

}

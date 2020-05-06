#include <stdio.h>  /* Need for standard I/O functions */

int NchooseK(int n, int k);

int main(){
   int n, k;
   while(n != 0){
     scanf("%d", &n);
     if(n == 0){
	return 0;
     }
     scanf("%d", &k);
     printf("%d\n", NchooseK(n,k));
   }
  return 0;
}

int NchooseK(int n, int k){
   if(k == 0 || k == n){
     return 1;
   }
   else{
     return NchooseK(n-1, k-1) + NchooseK(n-1, k);
   }
}

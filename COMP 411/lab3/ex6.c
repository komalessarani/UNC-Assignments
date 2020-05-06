 
#include <stdio.h>  /* Need for standard I/O functions */
#include <string.h> /* Need for strlen() */

int NchooseK();
void main()
{
   int n;
   int k;
   while(n != 0){
     printf("Enter two integers (for n and k) separated by space:\n");
     scanf("%d%d", &n, &k);
     printf("%d\n", NchooseK(n, k));
   }
}

int NchooseK(int num, int kay){
   if(kay == 0 || kay == num){
     return 1;
   }
   else{
     return NchooseK(num-1, kay-1) + NchooseK(num-1, kay);
   }
}

#include <stdio.h>

int main()
{
  int num[6];

  printf("Enter six integers:\n");
  for(int i = 0; i < 6; i++){
    scanf("%d", &num[i]);
  }
  printf("1234567890bb1234567890\n");

  for(int i = 0; i < 6; i++){
    if(i != 0 && i % 2 == 0){
	printf("\n");
    }
    if(i % 2 == 1){
        printf("  ");
    }
    printf("%10d", num[i]);
  }
    printf("\n");
}


/*  Exercise 1 */

#include <stdio.h>

int main()
{
  int num;
  char* ending;

  printf("Enter a number from 1 to 20:\n");
  scanf("%d", &num);

  //if true
  if(num >= 1 && num <= 20){
    printf("Here are the first %d ordinal numbers:\n", num);
    for(int i = 1; i <= num; i++){
       if(i == 1){
         ending = "st";
       } else if(i == 2){
         ending = "nd";
       } else if(i == 3){
	 ending = "rd";
       } else{
	 ending = "th";
       }
       printf("%d%s\n", i, ending);
    }
  } else {
    printf("Number is not in the range from 1 to 20\n");
  }
}


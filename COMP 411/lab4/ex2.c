/* Example: bubble sort strings in array */

#include <stdio.h>  /* Need for standard I/O functions */
#include <string.h> /* Need for strlen() */


#define NUM 25   /* number of strings */
#define LEN 1000  /* max length of each string */

int main()
{
  char Strings[NUM][LEN] = {0};
  char temp[LEN];

  printf("Please enter %d strings, one per line:\n", NUM);
  for(int i = 0; i < NUM; i++){
	fgets(Strings[i], LEN, stdin);
  }
  puts("\nHere are the strings in the order you entered:");

  /* Write a for loop here to print all the strings. */
  for(int j = 0; j < NUM; j++){
     	printf("%s", Strings[j]);
  }
  /* Bubble sort */
   for(int i = 0; i < NUM-1; i++){
     for(int j = i+1; j < NUM; j++){
        if(strcmp(Strings[i],Strings[j]) > 0){
	    strncpy(temp, Strings[i], LEN);
            strncpy(Strings[i], Strings[j], LEN);
	    strncpy(Strings[j], temp, LEN);
	}
     }
   }
  /* Output sorted list */
  puts("\nIn alphabetical order, the strings are:");     
  for(int i = 0; i < NUM; i++){
    printf("%s", Strings[i]);
  }
  return 0;
}

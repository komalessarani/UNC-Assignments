/* Example: bubble sort strings in array */

#include <stdio.h>  /* Need for standard I/O functions */
#include <string.h> /* Need for strlen() */


#define NUM 25   /* number of strings */
#define LEN 1000  /* max length of each string */


int my_compare_strings(char string1[], char string2[]) {

  for(int i = 0; i < LEN; i++){
	if(string1[i] < string2[i]){
	  return -1;
	}
	else if(string1[i] > string2[i]){
	  return 1;
	}
  }
  return 0;
}


void my_swap_strings(char string1[], char string2[]) {
  char temp[LEN];    // char variable used in swapping one character at a time

  for(int i = 0; i < LEN; i++){
	temp[i] = string1[i];
	string1[i] = string2[i];
	string2[i] = temp[i];
  }
}


int main()
{
  char Strings[NUM][LEN] = {0};

  printf("Please enter %d strings, one per line:\n", NUM);
  for(int i = 0; i < NUM; i++){
	fgets(Strings[i], LEN, stdin);
  }
  puts("\nHere are the strings in the order you entered:");

  /* Write a for loop here to print all the strings. */
  for(int j = 0; j < NUM; j++){
     	printf("%s", Strings[j]);
  }
   for(int i = 0; i < NUM-1; i++){
     for(int j = i+1; j < NUM; j++){
        if((my_compare_strings(Strings[i], Strings[j])) == 1){
	   my_swap_strings(Strings[i], Strings[j]);
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

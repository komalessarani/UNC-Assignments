/* Example: analysis of text */

#include <stdio.h>
#include <string.h>

#define MAX 1000 /* The maximum number of characters in a line of input */

int main()
{
  char text[MAX], c;
  char reverse[MAX];
  int j = 0, i = 0, length = 0;
  int palindrome = 1;

  puts("Type some text (then ENTER):");
  
  /* Save typed characters in text[]: */
    
    fgets(text, MAX, stdin);
    length = strlen(text)-1;

  /* Reverse the text and determine if it is a palindrom: */
     for(i = length-1; i >= 0; i--){
	reverse[j++] = text[i];
     }
     for(i = 0; i < length; i++){
        if(reverse[i] != text[i]){
           palindrome = 0;
        }
     }
     reverse[i] = '\0';
     puts("Your input in reverse is:");
     puts(reverse);
     
     if(palindrome){
       puts("Found a palindrome!");
     }
}

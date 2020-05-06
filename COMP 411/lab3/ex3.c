  
/* Example: analysis of text */

#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAX 1000 /* The maximum number of characters in a line of input */

int main()
{
  char text[MAX];
  char ltext[MAX], ntext[MAX];
  char reverse[MAX], nreverse[MAX];
  int length = 0;
  int palindrome = 1;

  puts("Type some text (then ENTER):");

  /* Save typed characters in text[]: */

    fgets(text, MAX, stdin);
    length = strlen(text)-1;

  /* Reverse the text and print it */
     int j = 0;
     for(int i = length-1; i >= 0; i--){
        reverse[j++] = text[i];
     }
     reverse[length] = '\0';
     puts("Your input in reverse is:");
     puts(reverse);

     //change the original text to all lowercase
     for(int k = 0; k < length; k++){
        ltext[k] = tolower(text[k]);
     }

     //remove the punctuation
     int index = 0;
     for(int n = 0; n < strlen(text); n++){
        if(isalpha(ltext[n])){
           ntext[index] = ltext[n];
           index++;
        }
	else if(!isspace(ltext[n])){
	   ntext[index] = ltext[n];
	   if(!ispunct(ltext[n])){
	   ntext[index] = ltext[n];
	   index++;
	}
       }
       else if(!ispunct(ltext[n])){
	  ntext[index] = ltext[n];
          if(!isspace(ltext[n])){
	     ntext[index] = ltext[n];
             index++;
	   }
       }
     }
    //reverse new array
    int r = 0;
    for(int m = strlen(ntext)-1; m >= 0; m--){
        nreverse[r++] = ntext[m];
    }
        nreverse[length] = '\0';

    for(int l = 0; l < strlen(ntext); l++){
        if(nreverse[l] != ntext[l]){
           palindrome = 0;
        }
     }

     if(palindrome){
       puts("Found a palindrome!");
     }
}


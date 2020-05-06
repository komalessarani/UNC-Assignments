#include <stdio.h>

void makepatterns(int N, int currentlevel, char pattern[]) {
	if(currentlevel==N)
		printf("%s\n", pattern);          // print pattern
	else {
		pattern[currentlevel]='0';               // set one character to something
		makepatterns(N, currentlevel+1, pattern);      // recursive call
		pattern[currentlevel]='1';               // set it to something else
		makepatterns(N, currentlevel+1, pattern);      // recursive call
	}
}

int main() {
	char pattern[21];               // at most 20 chars plus NULL
	int N;
	scanf("%d", &N);                // read N
	pattern[N]='\0';                // terminate string at position N
	makepatterns(N, 0, pattern);    // generate all patterns
}

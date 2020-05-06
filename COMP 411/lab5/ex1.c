#include<stdio.h>
#include<stdlib.h>

#define true 1
#define false 0

#define MAX 100

int maze[100][100];             // 100x100 is the maximum size needed
int wasHere[100][100];
int correctPath[100][100];
int width, height;
int startX, startY, endX, endY;

int recursiveSolve(int x, int y);

int main() {

  int x, y;
  scanf("%d%d", &width, &height);
  scanf("\n");    // This is needed to "eat" the newline after height,
                  // before the actual maze entries begin on the next line


  /* NOTE:  maze[y][x] will refer to the (x,y) element of the maze,
	   i.e., y-th row and x-th column in the maze.
     The row is typically the first index in a 2D array because
     reading and writing is done row-wise.  This is called
     "row-major" order.
     Also note that although we have declared the maze to be 100x100,
     that is the maximum size we need.  The actual entries in the
     maze will be height * width.
  */

  char tempchar;

  for(y=0; y < height; y++) {
    for(x=0; x < width; x++) {
      scanf("%c", &tempchar);
      maze[y][x]=tempchar;

     // Check for 'S' and 'F' here and use that to
    // set values of startX, startY, endX and endY

     if(tempchar == 'S'){
	startX = x;
        startY = y;
     }
     else if(tempchar == 'F'){
	endX = x;
	endY = y;
     }
     wasHere[y][x] = false;
     correctPath[y][x] = false;
    }
    scanf("\n");    // This is used to "eat" the newline
  }

  recursiveSolve(startX, startY);

 // Code to print the output maze
  for(int i = 0; i < height; i++){
    for(int j = 0; j < width; j++){
       if(correctPath[i][j] && maze[i][j] != 'S'){
	  maze[i][j] = '.';
       }
       printf("%c", maze[i][j]);
    }
    printf("\n");
  }
}



int recursiveSolve(int x, int y) {
  /* You can pretty much use the body of the code in the java version
      with almost no changes at all!  I only had to change one line!
  */
    if (x == endX && y == endY) return true; // If you reached the end
    if (maze[y][x] == '*' || wasHere[y][x]) return false;  
    // If you are on a wall or already were here
    wasHere[y][x] = true;
    if (x != 0) // Checks if not on left edge
        if (recursiveSolve(x-1, y)) { // Calls method one to the left
            correctPath[y][x] = true; // Sets that path value to true;
            return true;
        }
    if (x != width - 1) // Checks if not on right edge
        if (recursiveSolve(x+1, y)) { // Calls method one to the right
            correctPath[y][x] = true;
            return true;
        }
    if (y != 0)  // Checks if not on top edge
        if (recursiveSolve(x, y-1)) { // Calls method one up
            correctPath[y][x] = true;
            return true;
        }
    if (y != height - 1) // Checks if not on bottom edge
        if (recursiveSolve(x, y+1)) { // Calls method one down
            correctPath[y][x] = true;
            return true;
        }
    return false;
}

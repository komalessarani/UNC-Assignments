
#include <stdio.h>

int main()
{
  int width = 0;
  int height = 0;

  do{
      printf("Please enter width and height:\n");
      scanf("%d", &width);
      if(width == 0){
        printf("End\n");
 	break;
      }
      scanf("%d", &height);

    for(int j = 0; j < height; j++){
      for(int i = 0; i < width; i++){
	  if(i == 0){
	   if(j != 0 && j != height-1 && height > 2){
	     if(width == 1){
	 	printf("|\n");
	     }else{
	        printf("|");
	     }
	   }
	   else{
	     if(width == 1){
	       printf("+\n");
	     }else{
	        printf("+");	
	     }
	   }
	  }
	  else if(i == width-1){
	    if(j != 0 && j!= height-1 && height >2){
	      printf("|\n");
	    }
	    else{
	      printf("+\n");
	    }
	  }
	  else{
	   if(height > 2 && j !=0 && j!= height-1){
	     printf("~");
 	   }
	   else{
	     printf("-");
	   }
	  }
      }
    }
  }while(width!=0);
}


/*  Example: C program to find area of a circle */

#include <stdio.h>
#define PI 3.14159

int main()
{
  float r, a, c; 
  printf("Enter radius (in cm):\n");
  scanf("%f", &r);
  r = r/2.54;
  c = 2*PI*r;
  a = (PI * r * r);

  printf("Circle's area is %3.2f (sq in).\n", a);
  printf("Its circumference is %.2f (in).\n", c);
}

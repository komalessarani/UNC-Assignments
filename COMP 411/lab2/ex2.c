#include <stdio.h>

int main()
{
  double num = 0.0;
  double sum = 0.0, min = 0.0, max = 0.0, product = 1.0;

  printf("Enter 10 floating-point numbers:\n");
  for(int i = 0; i < 10; i++){
    scanf("%lf", &num);
    sum += num;
    if(num < min){
	min = num;
    }
    if(num > max){
	max = num;
    }
    product = product*num;
  }
  printf("Sum is %.5f\n", sum);
  printf("Min is %.5f\n", min);
  printf("Max is %.5f\n", max);
  printf("Product is %.5f\n", product);

}

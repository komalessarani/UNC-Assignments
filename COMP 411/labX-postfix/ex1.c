#include <stdio.h>
#include <string.h>

struct stack {
    int stack[20];
    int top;
} s;

void push(int e);
int pop();
void clear();

char postfix[32];
int get_next(char * postfix, int * p);

int main()
{
    while(1)
    {
        fgets(postfix, 32, stdin);
        if (postfix[0] == '0') break;
        clear();
        int p = 0, r, temp; // p for postion within postfix
        do {
            // printf("r = %d\n", r = get_next(postfix, &p));
            switch (r = get_next(postfix, &p)) {
                case -1: temp = pop(); push(pop() + temp); break;
                case -2: temp = pop(); push(pop() - temp); break;
                case -3: temp = pop(); push(pop() * temp); break;
                case -4: temp = pop(); push(pop() / temp); break;
                case -5: printf("%d\n", pop()); break;
                default: push(r);
            }
        } while (r != -5);
    }
}

int get_next(char * postfix, int * p) {
    // consumes leading spaces but not trailing spaces
    // -1 for +, -2 for -, -3 for *, -4 for /, and -5 for terminate.
    while (*p < strlen(postfix) && postfix[*p] == ' ') (*p)++;
    switch (postfix[*p]) {
        case '+': (*p)++; return -1;
        case '-': (*p)++; return -2;
        case '*': (*p)++; return -3;
        case '/': (*p)++; return -4;
        case '\n': return -5;
    }
    int temp = 0;
    while (*p < strlen(postfix) && postfix[*p] >= '0' && postfix[*p] <= '9') {
        temp = temp * 10 + postfix[(*p)++] - '0';
    }
    return temp;
}

void push(int e) {
    s.stack[s.top++] = e;
}
int pop() {
    return s.stack[--s.top];
}
void clear() {
    s.top = 0;
}

%option noyywrap

%{
#include <stdio.h>

int flag = 0;
int underflow = 0;
int line = 1;
%}

%%
"(" {
    flag++;
}
")" {
    if (flag == 0)
        underflow = 1;
    else
        flag--;
}
[\n] {
    if (!underflow && flag == 0)
        printf("Line %d: Parentheses are balanced.\n", line);
    if (underflow)
        printf("Line %d: Missing '(' parenthesis.\n", line);
    if (flag > 0)
        printf("Line %d: Missing ')' parenthesis.\n", line);
    flag = 0;
    underflow = 0;
    line++;
}
. {
    /* Ignore other characters */
}
%%

int main()
{
    yylex();
    return 0;
}

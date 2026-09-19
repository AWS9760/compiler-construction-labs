%option noyywrap

%{
#include <stdio.h>
%}

%%
[\n] {
    printf("Hello World\n");
}
%%

int main()
{
    yylex();
    return 0;
}

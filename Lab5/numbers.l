%option noyywrap

%{
#include <stdio.h>
%}

%%
[0-9]+ {
    printf("NUMBER: %s\n", yytext);
}
[ \t\n]+ {
    /* Ignore whitespace */
}
. {
    printf("OTHER: %s\n", yytext);
}
%%

int main()
{
    yylex();
    return 0;
}

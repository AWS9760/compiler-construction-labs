%option noyywrap

%{
#include <stdio.h>
#include <stdlib.h>

double op1 = 0, op2 = 0;
char oper = 0;
int state = 0;
int line = 1;

void calculate(void);
%}

DIGIT [0-9]
NUM   {DIGIT}+(\.{DIGIT}+)?
OP    [*/+-]

%%
{NUM} {
    if (state == 0) {
        op1 = atof(yytext);
        state = 1;
    } else if (state == 2) {
        op2 = atof(yytext);
        state = 3;
    } else {
        state = -1;
    }
}
{OP} {
    if (state == 1) {
        oper = yytext[0];
        state = 2;
    } else {
        state = -1;
    }
}
[ \t\r]+ {
    /* Ignore whitespace */
}
\n {
    calculate();
}
<<EOF>> {
    if (state != 0)
        calculate();
    return 0;
}
. {
    state = -1;
}
%%

void calculate(void)
{
    double result;

    if (state == 3) {
        switch (oper) {
            case '+': result = op1 + op2; break;
            case '-': result = op1 - op2; break;
            case '*': result = op1 * op2; break;
            default:
                if (op2 == 0) {
                    printf("Line %d: Division by zero.\n", line);
                    state = 0;
                    line++;
                    return;
                }
                result = op1 / op2;
        }
        printf("The answer is = %.10g\n", result);
    } else if (state != 0) {
        printf("Line %d: Invalid expression.\n", line);
    }
    state = 0;
    line++;
}

int main()
{
    yylex();
    return 0;
}

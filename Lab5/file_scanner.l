%option noyywrap

%{
#include <stdio.h>
%}

DIGIT  [0-9]
LETTER [A-Za-z_]
ID     {LETTER}({LETTER}|{DIGIT})*

%%
"int"      { printf("KEYWORD: %s\n", yytext); }
"float"    { printf("KEYWORD: %s\n", yytext); }
"char"     { printf("KEYWORD: %s\n", yytext); }
"if"       { printf("KEYWORD: %s\n", yytext); }
"else"     { printf("KEYWORD: %s\n", yytext); }
"while"    { printf("KEYWORD: %s\n", yytext); }
"for"      { printf("KEYWORD: %s\n", yytext); }
"return"   { printf("KEYWORD: %s\n", yytext); }

{ID}       { printf("IDENTIFIER: %s\n", yytext); }
{DIGIT}+(\.{DIGIT}+)? { printf("NUMBER: %s\n", yytext); }

"+"        { printf("ARITHMETIC OPERATOR: %s\n", yytext); }
"-"        { printf("ARITHMETIC OPERATOR: %s\n", yytext); }
"*"        { printf("ARITHMETIC OPERATOR: %s\n", yytext); }
"/"        { printf("ARITHMETIC OPERATOR: %s\n", yytext); }
"=="       { printf("RELATIONAL OPERATOR: %s\n", yytext); }
"!="       { printf("RELATIONAL OPERATOR: %s\n", yytext); }
"<="       { printf("RELATIONAL OPERATOR: %s\n", yytext); }
">="       { printf("RELATIONAL OPERATOR: %s\n", yytext); }
"<"        { printf("RELATIONAL OPERATOR: %s\n", yytext); }
">"        { printf("RELATIONAL OPERATOR: %s\n", yytext); }
"="        { printf("ASSIGNMENT OPERATOR: %s\n", yytext); }

";"        { printf("DELIMITER: %s\n", yytext); }
","        { printf("DELIMITER: %s\n", yytext); }
"("        { printf("DELIMITER: %s\n", yytext); }
")"        { printf("DELIMITER: %s\n", yytext); }
"{"        { printf("DELIMITER: %s\n", yytext); }
"}"        { printf("DELIMITER: %s\n", yytext); }

[ \t\r\n]+ { /* Ignore whitespace */ }
.          { printf("UNKNOWN: %s\n", yytext); }
%%

int main(int argc, char *argv[])
{
    FILE *file;
    const char *name = (argc > 1) ? argv[1] : "input.txt";

    file = fopen(name, "r");
    if (file == NULL)
    {
        printf("Unable to open file.\n");
        return 1;
    }

    yyin = file;
    yylex();
    fclose(file);
    return 0;
}

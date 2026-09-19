%option noyywrap

%{
#include <stdio.h>
#include <string.h>

#define MAX_TOKENS 5000

typedef struct {
    int  line;
    char lexeme[128];
    char type[16];
} Token;

Token table[MAX_TOKENS];
int token_count = 0;
int error_count = 0;
int line_no = 1;
int comment_start = 0;

void add_token(const char *category, const char *type, const char *lexeme);
void lex_error(const char *message, const char *lexeme);
void print_table(void);

#define TOKEN(category, type) add_token(category, type, yytext)
%}

DIGIT   [0-9]
LETTER  [A-Za-z_]
ID      {LETTER}({LETTER}|{DIGIT})*
INTEGER {DIGIT}+
FLOAT   {DIGIT}+\.{DIGIT}+

%x COMMENT

%%
"int"|"float"|"double"|"char"|"void"|"if"|"else"|"while"|"for"|"return"|"break"|"continue" {
    TOKEN("KEYWORD", "KEYWORD");
}

{ID} {
    TOKEN("IDENTIFIER", "IDENTIFIER");
}

{FLOAT} {
    TOKEN("FLOAT", "FLOAT");
}

{INTEGER} {
    TOKEN("INTEGER", "INTEGER");
}

{DIGIT}+{LETTER}({LETTER}|{DIGIT})* {
    lex_error("invalid identifier", yytext);
}

{DIGIT}+(\.{DIGIT}+){2,} {
    lex_error("malformed number", yytext);
}

"+="|"-="|"*="|"/="|"=" {
    TOKEN("ASSIGNMENT OPERATOR", "ASSIGNMENT");
}

"=="|"!="|"<="|">="|"<"|">" {
    TOKEN("RELATIONAL OPERATOR", "RELATIONAL");
}

"&&"|"||"|"!" {
    TOKEN("LOGICAL OPERATOR", "LOGICAL");
}

"+"|"-"|"*"|"/"|"%" {
    TOKEN("ARITHMETIC OPERATOR", "ARITHMETIC");
}

[;,(){}\[\]] {
    TOKEN("DELIMITER", "DELIMITER");
}

"//".* {
    /* Ignore single-line comment */
}

"/*" {
    comment_start = line_no;
    BEGIN(COMMENT);
}

<COMMENT>"*/" {
    BEGIN(INITIAL);
}

<COMMENT>\n {
    line_no++;
}

<COMMENT>. {
    /* Ignore comment text */
}

<COMMENT><<EOF>> {
    printf("Line %d LEXICAL ERROR: unterminated comment (started at line %d)\n",
           line_no, comment_start);
    error_count++;
    BEGIN(INITIAL);
    return 0;
}

[ \t\r]+ {
    /* Ignore whitespace */
}

\n {
    line_no++;
}

. {
    lex_error("invalid character", yytext);
}
%%

void add_token(const char *category, const char *type, const char *lexeme)
{
    printf("Line %d %s %s\n", line_no, category, lexeme);

    if (token_count < MAX_TOKENS) {
        table[token_count].line = line_no;
        strncpy(table[token_count].lexeme, lexeme, sizeof(table[0].lexeme) - 1);
        table[token_count].lexeme[sizeof(table[0].lexeme) - 1] = '\0';
        strcpy(table[token_count].type, type);
        token_count++;
    }
}

void lex_error(const char *message, const char *lexeme)
{
    printf("Line %d LEXICAL ERROR: %s '%s'\n", line_no, message, lexeme);
    error_count++;
}

void print_table(void)
{
    const char *types[] = {
        "KEYWORD", "IDENTIFIER", "INTEGER", "FLOAT", "ARITHMETIC",
        "RELATIONAL", "LOGICAL", "ASSIGNMENT", "DELIMITER"
    };
    int n_types = sizeof(types) / sizeof(types[0]);
    int i, j, count;

    printf("\n================ TOKEN TABLE ================\n");
    printf("%-6s %-14s %s\n", "Line", "Lexeme", "Token");
    printf("---------------------------------------------\n");
    for (i = 0; i < token_count; i++)
        printf("%-6d %-14s %s\n", table[i].line, table[i].lexeme, table[i].type);

    printf("\n================== SUMMARY ==================\n");
    for (i = 0; i < n_types; i++) {
        count = 0;
        for (j = 0; j < token_count; j++)
            if (strcmp(table[j].type, types[i]) == 0)
                count++;
        printf("%-14s : %d\n", types[i], count);
    }
    printf("---------------------------------------------\n");
    printf("%-14s : %d\n", "Total tokens", token_count);
    printf("%-14s : %d\n", "Lexical errors", error_count);
}

int main(int argc, char *argv[])
{
    FILE *file;
    const char *name = (argc > 1) ? argv[1] : "input.c";

    file = fopen(name, "r");
    if (file == NULL) {
        printf("Unable to open file: %s\n", name);
        return 1;
    }

    yyin = file;
    yylex();
    fclose(file);

    print_table();
    return 0;
}

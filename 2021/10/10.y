%{
    #include <stdio.h>
    #include <stdlib.h>
    #include <stdbool.h>

    char *input;

    /* forward decl; defined in lexer */
    void setScannerInput(char *input);
    void finalizeScanner();    
    int yylex(void);
    void yyerror(const char *s);
    void setlinescore(int tok);

    int errorcount = 0;
    unsigned long long linescore = 0;
    bool incomplete = false;
%}

%define parse.error verbose

%token LPAR RPAR LSQ RSQ LANGLE RANGLE LCURL RCURL NEWLINE
%start Start

%%

Start : SS NEWLINE 
;

SS : SS S
   | 
   ;

S : LPAR SS RPAR
  | LSQ SS RSQ
  | LANGLE SS RANGLE
  | LCURL SS RCURL
  /* | LPAR error RPAR { if (yychar == NEWLINE) { setlinescore(RPAR); yyerrok; } }
  | LSQ error RSQ { if (yychar == NEWLINE) { setlinescore(RSQ); yyerrok; } }
  | LANGLE error RANGLE { if (yychar == NEWLINE) { setlinescore(RANGLE); yyerrok; } }
  | LCURL error RCURL { if (yychar == NEWLINE) { setlinescore(RCURL); yyerrok; } } */
  |
  ;

%%

/*
* the following code is copied verbatim
* in the generated C parser file 
*/

void setlinescore(int tok) {
    incomplete = true;
    linescore *= 5;
    switch (tok) {
        case RPAR:
            linescore += 1;
            break;
        case RSQ:
            linescore += 2;
            break;
        case RCURL:
            linescore += 3;
            break;
        case RANGLE:
            linescore += 4;
            break;
        default:
            break;
    }
}

void yyerror(const char *msg) {
    if (incomplete) return;
    /* printf("%s\n", msg); */
    if (yychar == RPAR) errorcount += 3;
    else if (yychar == RSQ) errorcount += 57;
    else if (yychar == RCURL) errorcount += 1197;
    else if (yychar == RANGLE) errorcount += 25137;
}

int main() {
    char filename[] = "input.txt";
    FILE *file = fopen(filename, "r");

    if (file != NULL) {
        char line [1000];
        while(fgets(line,sizeof line,file) != NULL) {
            setScannerInput(line);
            yyparse();
            if (linescore) printf("%llu,\n", linescore);
            linescore = 0;
            incomplete = false;
        }

        fclose(file);
    } else {
        perror(filename); //print the error message on stderr.
    }

    printf("corrupt: %d\n", errorcount);

    return 0;
}
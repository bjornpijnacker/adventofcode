all: parser lexer
	gcc parser.o lexer.o -lfl -lm

parser: 10.y
	bison -d 10.y
	mv 10.tab.c parser.c
	gcc -c parser.c

lexer: 10.fl
	flex 10.fl
	mv lex.yy.c lexer.c
	gcc -c lexer.c

clean:
	rm -f *~
	rm -f *.o
	rm -f parser.c
	rm -f parser.h
	rm -f lexer.c
	rm -f a.out

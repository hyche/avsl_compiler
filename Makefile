lexer:
	jflex genLexer.jflex
parser:
	java -jar java-cup-11b.jar -interface -locations genParser.cup
compile:
	javac -cp java-cup-11b-runtime.jar:. *.java
clear:
	rm *.class Lexer.java Parser.java ParserSym.java
all:
	jflex genLexer.jflex && java -jar java-cup-11b.jar -interface -parser Parser -locations genParser.cup && javac -cp java-cup-11b-runtime.jar:. *.java

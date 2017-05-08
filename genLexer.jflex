import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java.io.*;
/**
 *
 */

%%

%class Lexer
%implements ParserSym
//%function next_token
%type java_cup.runtime.Symbol
%public
%cup
%char
%line
%column

%{
    StringBuffer string = new StringBuffer();
    int isTranspose = 0;
    ComplexSymbolFactory symbolFactory;

    public Lexer(java.io.Reader in, ComplexSymbolFactory sf) {
        this(in);
        symbolFactory = sf;
    }

    private Symbol symbol(String name, int sym) {
        return symbolFactory.newSymbol(name, sym, new Location(yyline+1, yycolumn+1, yychar),
                                       new Location(yyline+1, yycolumn+yylength(), yychar+yylength()));
    }

    private Symbol symbol(String name, int sym, Object value) {
        Location left = new Location(yyline+1, yycolumn+1, yychar);
        Location right = new Location(yyline+1, yycolumn+yylength(), yychar+yylength());
        return symbolFactory.newSymbol(name, sym, left, right, value);
    }

    private Symbol symbol(String name, int sym, Object value, int buflength) {
        Location left = new Location(yyline+1, yycolumn+yylength()-buflength, yychar+yylength()-buflength);
        Location right = new Location(yyline+1, yycolumn+yylength(), yychar+yylength());
        return symbolFactory.newSymbol(name, sym, left, right, value);
    }

    private void error(String message) {
        System.out.println("error!");
    }
%}

%eofval{
    return symbolFactory.newSymbol("eof", ParserSym.EOF, new Location(yyline+1, yycolumn+1, yychar), new Location(yyline+1, yycolumn+1,yychar+1));
%eofval}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = [ \t\f]
Comment = "{" ~"}"
Identifier = _* [:jletter:] _* ( [:jletter:] | [:jletterdigit:] )* _*
IntegerLiteral = 0+ | [1-9][0-9]*

%state STRING

%%

<YYINITIAL> {
    /* key words */
    "if"            { return symbol("if", IF); }
    "then"          { return symbol("then", THEN); }
    "while"         { return symbol("while", WHILE); }
    "do"            { return symbol("do", DO); }
    "begin"         { return symbol("begin", BEGIN); }
    "end"           { return symbol("end", END); }
    "read"          { return symbol("read", READ); }
    "write"         { return symbol("write", WRITE); }

    /* literals */
    {Identifier}    { isTranspose = 1; return symbol("identifier", IDENTIFIER, yytext()); }
    {IntegerLiteral} { return symbol("IntConst", INTEGERLITERAL, new Integer(yytext())); }
    //\'              { string.setLength(0); yybegin(STRING); }

    /* separators */
    "="             { return symbol("assign", ASSIGN); }
    ";"             { return symbol("semicolon", SEMI, ParserSym.SEMI); }

    "+"             { return symbol("plus", PLUS, ParserSym.PLUS); }
    "-"             { return symbol("minus", MINUS, ParserSym.MINUS); }
    "*"             { return symbol("mult", MULT, ParserSym.MULT); }
    "/"             { return symbol("divide", DIVIDE, ParserSym.DIVIDE); }
    "%"             { return symbol("modular", MODULAR, ParserSym.MODULAR); }
    "("             { return symbol("lparen", LPAREN, ParserSym.LPAREN); }
    ")"             { return symbol("rparen", RPAREN, ParserSym.RPAREN); }
    "~"             { return symbol("sign", SIGN, ParserSym.SIGN); }


    //{LineTerminator} { return symbol("lineterminate", LT, ParserSym.LT); }
    /* comments */
    {LineTerminator} { }
    //"..."{LineTerminator}       { }
    {Comment}   { }
    {WhiteSpace} { }
}

//<STRING> {

//    [^\n\r\'\\]+    { string.append( yytext() ); }
//    \\t             { string.append('\t'); }
//    \\n             { string.append('\n'); }
//    \\r             { string.append('\r'); }
//    \\\"            { string.append('\"'); }
//    \\              { string.append('\\'); }
//}

/* error */
[^]     { throw new Error("Illegal character |" + yytext() + "|"); }

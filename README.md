# AVSL Compiler
A compiler for A Very Simple Language 

This compiler compile AVSL code to MIPS code. The code generated in file **a.out** by default, you can run MIPS code by using [Mars](http://courses.missouristate.edu/KenVollmar/mars/download.htm), a MIPS simulator.
The compiler use [Jflex](http://www.jflex.de/) for generating lexer/scanner and [CUP](http://www2.cs.tum.edu/projects/cup/) for generating parser.

First download jflex and CUP and follow their install instructions.
Then generate lexer, parser and compile all java files together. Read Makefile for more details.

To run a test case use this command:
```
  java -cp java-cup-11b-runtime.jar:. genMain your_test_case
```
**Notes:** If you are using Windows, make sure change **:** to **;** in the Makefile and the command to run test cases.

## AVSL grammar
You must code AVSL follow a grammar listed below, otherwise the compiler will crash, it doesn't have any error handlers. The grammar contains just basic rules.
 
 ```
ID → letter (letter | digit)*
NUM → digit+
letter → [a-z] | [A-Z]
digit → [0-9]

Program → Blockto 
Expr → ID | NUM | ‘(’ Expr ‘)’
Expr → Unop Expr
Unop → ‘-’ | '+' | '~'
Expr → Expr Binop Expr
Binop → ‘+’ | ‘-’ | ‘*’ | ‘/’ | ‘%’
Stmt → ID ‘=’ Expr ‘;’
Stmt → read ID ‘;’
Stmt → write Expr ‘;’
Stmt → if Expr then Stmt
Stmt → while Expr do Stmt
Stmt → Block
Block → begin Stmts end
Stmts → Stmt | Stmt Stmts
 ```


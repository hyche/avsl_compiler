## AVSL Compiler
A compiler for A Very Simple Language 

This compiler use [Jflex](http://www.jflex.de/) for generating lexer/scanner and [CUP](http://www2.cs.tum.edu/projects/cup/) for generating parser.

First download jflex and CUP and follow their install instructions.
Then generate lexer, parser and compile all java files together. Read Makefile for more details.

To run a test case use this command:
```
  java -cp java-cup-11b-runtime.jar:. genMain your_test_case
```

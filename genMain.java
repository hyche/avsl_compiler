import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ScannerBuffer;
import java.io.*;

class genMain {
    public static void main(String[] args) throws Exception{
        if(args[0] == "") {
            System.out.println("No file to open!");
        }

        ComplexSymbolFactory csf = new ComplexSymbolFactory();
        ScannerBuffer lexer = new ScannerBuffer(new Lexer(new BufferedReader(new FileReader(args[0])), csf));
        Parser p = new Parser(args[0], lexer, csf);
        Program prg = (Program) p.parse().value;
        prg.out();
    }
}

import java.util.*;
import java.io.*;

public class Program {
    public static Hashtable<String, Integer> symbolTable;
    public static Stack<String> registerHandler;
    public static Integer nextLabel;
    public static String fileName;
    private static Stmt.ListStmt sl;

    public Program(Stmt.ListStmt sl, String fileName) {
        this.sl = sl;
        this.fileName = fileName;
        symbolTable = new Hashtable<String, Integer>();
        registerHandler = new Stack<String>();
        nextLabel = 0;
    }

    public static String getNextLabel() {
        return String.format("L%d", nextLabel++);
    }

    public static void out() {
        System.out.println("Running ...");
        String main = sl.toString();
        String data = "";
        for (String s : symbolTable.keySet()) {
            data += "\t" + s + ":\t" + ".word\t" + "1\n";
        }
        data += "\t newline:\t.asciiz\t\"\\n\"\n";

        String frame = ".data\n%s.text\n\n.globl main\nmain:\n%s\n\taddi $v0, $zero, 10\n\tsyscall";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write( String.format(frame, data, main) );
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

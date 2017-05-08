import java_cup.runtime.ComplexSymbolFactory.Location;
import java.lang.*;
import java.util.*;

public abstract class Stmt implements ParserSym {
    private String name;
    public Stmt() {
        name = "";
    }

    public static EmptyStmt empty() { return new EmptyStmt(); }
    public static class EmptyStmt extends Stmt {
        public EmptyStmt() {}
        public String toString() { return ""; }
    }

    public static AssignStmt assign(Expr.Identifier id, Expr e) { return new AssignStmt(id, e); }
    public static class AssignStmt extends Stmt {
        private Expr e;
        private Expr.Identifier id;
        public AssignStmt(Expr.Identifier id, Expr e) {
            this.e = e;
            this.id = id;
        }
        public String toString() {
            Program.symbolTable.put(id.toString(), e.eval());
            String load_val = "";
            String ret = "%s\tsw $s0, %s\n\n";
            if (e instanceof Expr.IntegerConst) {
                load_val = "\tli $s0, " + e.toString() + "\n";
            } else if (e instanceof Expr.Identifier) {
                load_val = "\tlw $s0, " + e.toString() + "\n";
            } else {
                load_val += e.toString();
            }
            return String.format(ret, load_val, id.toString());
        }

    }

    public static WriteStmt write(Expr e) { return new WriteStmt(e); }
    public static class WriteStmt extends Stmt {
        private Expr e;
        public WriteStmt(Expr e) {
            this.e = e;
        }

        public String toString() {
            String ret = "%s\n\taddi $v0, $zero, 1\n\tsyscall\n\taddi $v0, $zero, 4\n\tla $a0, newline\n\tsyscall\n\n";
            String load_val = "";
            if (e instanceof Expr.IntegerConst) {
                load_val = "\tli $a0, " + e.toString();
            } else if (e instanceof Expr.Identifier) {
                //Program.symbolTable.put(e.toString, 0);
                load_val = "\tlw $a0, " + e.toString();
            } else {
                if (e == null) {
                    System.out.println("binex\n");
                }
                load_val = e.toString() + "\tmove $a0, $s0";
            }
            //ret = e.toString();
            return String.format(ret, load_val);
        }
    }

    public static ReadStmt read(Expr.Identifier i) { return new ReadStmt(i); }
    public static class ReadStmt extends Stmt {
        private Expr.Identifier i;
        public ReadStmt(Expr.Identifier i) {
            this.i = i;
        }

        public String toString() {
            Program.symbolTable.put(i.toString(), 0);
            String ret = String.format("\taddi $v0, $zero, 5\n\tsyscall\n\tsw $v0, %s\n\n", i.toString());
            return ret;
        }
    }

    public static ListStmt liststmt(LinkedList<Stmt> sl) {
        return new ListStmt(sl);
    }
    public static class ListStmt extends Stmt {
        private LinkedList<Stmt> sl;
        public ListStmt(LinkedList<Stmt> sl) {
            this.sl = sl;
        }

        public String toString() {
            String ret = "";
            while( sl.size() != 0 ) {
                Stmt s = sl.removeFirst();
                ret += s.toString();
            }
            return ret;
        }

        public void add(Stmt s) {
            sl.add(s);
        }

    }


    public static IfThenStmt ifthen(Stmt s, Expr c) { return new IfThenStmt(s, c); } 
    public static class IfThenStmt extends Stmt {
        private Stmt s;
        private Expr c;
        public IfThenStmt(Stmt s, Expr c) {
            this.s = s;
            this.c = c;
        }
        public String toString() {
            String ifFalse = Program.getNextLabel();
            String ret = "";
            //String ret = String.format("\n%s\n", ifFalse);
            if (c instanceof Expr.IntegerConst) {
                ret = "\tli $t0, " + c.toString() + "\n";
            } else if (c instanceof Expr.Identifier) {
                ret = "\tlw $t0, " + c.toString() + "\n";
            } else {
                ret = c.toString() + "\tmove $t0, $s0\n";
            }
            ret += "\tbeq $t0, $zero, " + ifFalse + "\n";
            ret += s.toString();
            ret += ifFalse + ":\n";
            return ret;
        }
    }

    public static WhileStmt whileloop(Stmt s, Expr c) { return new WhileStmt(s, c); } 
    public static class WhileStmt extends Stmt {
        private Stmt s;
        private Expr c;
        public WhileStmt(Stmt s, Expr c) {
            this.s = s;
            this.c = c;
        }
        public String toString() {
            String begin = Program.getNextLabel();
            String ifFalse = Program.getNextLabel();
            String ret = begin + ":\n";

            if (c instanceof Expr.IntegerConst) {
                ret += "\tli $t0, " + c.toString() + "\n";
            } else if (c instanceof Expr.Identifier) {
                ret += "\tlw $t0, " + c.toString() + "\n";
            } else {
                ret += c.toString() + "\tmove $t0, $s0\n";
            }
            ret += "\tbeq $t0, $zero, " + ifFalse + "\n";
            ret += s.toString() + "\tj " + begin + "\n";
            ret += ifFalse +":\n";
            return ret;
        }
    }
}

import java_cup.runtime.ComplexSymbolFactory.Location;
import java.lang.*;
import java.util.*;

public abstract class Expr implements ParserSym {
    public Expr() {} 
    public abstract Integer eval();

    public static EmptyExpr empty() {
        return new EmptyExpr();
    }

    public static class EmptyExpr extends Expr {
        public EmptyExpr() {}
        public String toString() { return ""; }
        public Integer eval() {
            return 0;
        }
    }

    public static IntegerConst intconst(Integer i) { return new IntegerConst(i); }
    public static class IntegerConst extends Expr {
        private Integer value;

        public IntegerConst(Integer v) { this.value = v; }
        public String toString() { return Integer.toString(value); }
        public Integer eval(){
            return value;
        }
    }

    public static Expr binop(Expr e1, int op, Expr e2){ 
        if (e1 instanceof IntegerConst && e2 instanceof IntegerConst) {
            Integer value = 0;
            switch(op) {
            case MINUS: value = e1.eval() - e2.eval(); break;
            case PLUS: value = e1.eval() + e2.eval(); break;
            case MULT: value = e1.eval() * e2.eval(); break;
            case DIVIDE: value = e1.eval() / e2.eval(); break;
            case MODULAR: value = e1.eval() % e2.eval(); break;
            default:
            break;
            }
            System.out.println(value);
            return new IntegerConst(value);
        }
        return new Binex(e1,op,e2);
    }
    public static class Binex extends Expr {
        private Expr e1, e2;
        private int op;
        public Binex(Expr e1, int op, Expr e2) {
            this.e1=e1;
            this.e2=e2;
            this.op=op;
        }

        public String toString(){
            String ret = "";

            if (e1 instanceof IntegerConst && !(e2 instanceof Binex)) {
                ret = "\tli $t0, " + e1.toString() + "\n";
            } else if (e1 instanceof Identifier && !(e2 instanceof Binex)) {
                Program.symbolTable.put(e1.toString(), 0);
                ret = "\tlw $t0, " + e1.toString() + "\n";
            } else if (e1 instanceof Binex){
                ret = e1.toString();
                if (!(e2 instanceof Binex)) {
                    ret += "\tmove $t0, $s0\n";
                } else {
                    int size = Program.registerHandler.size();
                    String saveReg = String.format("$t%d", size+2); // saved register start at 2
                    Program.registerHandler.push(saveReg);
                    ret += String.format("\tmove %s, $s0\n", saveReg);
                }
            }

            if (e2 instanceof IntegerConst) {
                ret += "\tli $t1, " + e2.toString() + "\n";
            } else if (e2 instanceof Identifier) {
                Program.symbolTable.put(e2.toString(), 0);
                ret += "\tlw $t1, " + e2.toString() + "\n";
            } else {
                System.out.println(e2.toString());
                ret += e2.toString() + "\tmove $t1, $s0\n";
                if (e1 instanceof IntegerConst) {
                    ret += "\tli $t0, " + e1.toString() + "\n";
                } else if (e1 instanceof Identifier) {
                    ret += "\tlw $t0, " + e1.toString() + "\n";
                } else {
                    String saveReg = Program.registerHandler.pop();
                    ret += String.format("\tmove $t0, %s\n", saveReg);
                }
            }

            switch(op) {
            case MINUS:
                ret += "\tsub $s0, $t0, $t1\n";
            break;
            case PLUS:
                ret += "\tadd $s0, $t0, $t1\n";
            break;
            case MULT:
                ret += "\tmul $s0, $t0, $t1\n";
            break;
            case DIVIDE:
                ret += "\tdiv $s0, $t0, $t1\n";
            break;
            case MODULAR:
                ret += "\tdiv $t0, $t1\n\tmfhi $s0\n";
            break;
            case SIGN:
                String label = Program.getNextLabel();
                ret += "\taddi $s0, $zero, 1\n\tbgez $t1, " + label + "\n\taddi $s0, $zero, 0\n\t" + label + ":\n";
            break;
            default:
            break;
            }
            System.out.println(ret);
            return ret;
        }

        public Integer eval() {
            Integer value = 0;
            switch(op) {
            case MINUS: value = e1.eval() + e2.eval(); break;
            case PLUS: value = e1.eval() - e2.eval(); break;
            case MULT: value = e1.eval() * e2.eval(); break;
            case DIVIDE: value = e1.eval() / e2.eval(); break;
            case MODULAR: value = e1.eval() % e2.eval(); break;
            default:
            break;
            }
            return value;
        }
    }

    public static Expr unop(Expr e, int op) {
        if (e instanceof IntegerConst) {
            Integer value = 0;
            switch(op) {
            case MINUS: value = -e.eval(); break;
            case PLUS: value = e.eval(); break;
            case SIGN:
                if (e.eval() > 0)
                    value = 1;
                else
                    value = 0;
            break;
            default:
            break;
            }
            return new IntegerConst(value);
        }
        return new Binex(new IntegerConst(0), op, e);
    }

    public static Identifier ident(Location l, String s, Location r){ return new Identifier(l, s, r); }
    public static class Identifier extends Expr {
        private Location left, right;
        private String i;
        public Identifier(Location l, String i,Location r){
            this.i=i;
            this.left=l;
            this.right=r;
            /*
            if(!Program.symbolTable.containsKey(i)) {
                Program.symbolTable.put(i, null);
            }
            */
        }
        public String toString() {
            return i;
        }

        public Integer eval() {
            return Program.symbolTable.get(i);
        }
    }
}

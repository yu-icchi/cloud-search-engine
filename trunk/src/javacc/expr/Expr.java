package javacc.expr;

import javacc.expr.parser.*;
import java.io.*;

public class Expr implements ExprParserVisitor{
    public static void main(String[] args) throws ParseException, IOException{
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(in);
        String line;
        while((line = reader.readLine()) != null){
            ExprParser parser = new ExprParser(new StringReader(line));
            Expr visitor = new Expr();
            ASTStart start = parser.Start();
            System.out.println(start.jjtAccept(visitor, null));
        }
    }

    public Object visit(SimpleNode node, Object data) {
        return null; //ここには来ない
    }

    /** 開始記号 */
    public Object visit(ASTStart node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    /** 足し算 */
    public Object visit(ASTAdd node, Object data) {
        Integer left = (Integer) node.jjtGetChild(0).jjtAccept(this, null);
        Integer right = (Integer) node.jjtGetChild(1).jjtAccept(this, null);

        return left + right;
    }

    /** 引き算 */
    public Object visit(ASTSub node, Object data) {
        Integer left = (Integer) node.jjtGetChild(0).jjtAccept(this, null);
        Integer right = (Integer) node.jjtGetChild(1).jjtAccept(this, null);

        return left - right;
    }

    /** 数値リテラル */
    public Object visit(ASTInteger node, Object data) {
        String value = node.nodeValue;
        return Integer.valueOf(value);
    }
}
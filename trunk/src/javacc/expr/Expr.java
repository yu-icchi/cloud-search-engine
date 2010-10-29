package javacc.expr;

import javacc.expr.parser.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Expr implements ExprParserVisitor{
    public static void main(String[] args) throws ParseException, IOException{
        String line = "13+5*3-(2+1)/4";
        System.out.println(line);
        ExprParser parser = new ExprParser(new StringReader(line));
        Expr visitor = new Expr();
        ASTStart start = parser.Start();
        System.out.println(start.jjtAccept(visitor, null));
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
        Map<String, String> map = new HashMap<String, String>();
        map.put("logic", "ADD");
        map.put("left", node.jjtGetChild(0).jjtAccept(this, null).toString());
        map.put("right", node.jjtGetChild(1).jjtAccept(this, null).toString());
        System.out.println(map);
        return left + right;
    }

    /** 引き算 */
    public Object visit(ASTSub node, Object data) {
        Integer left = (Integer) node.jjtGetChild(0).jjtAccept(this, null);
        Integer right = (Integer) node.jjtGetChild(1).jjtAccept(this, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put("logic", "SUB");
        map.put("left", node.jjtGetChild(0).jjtAccept(this, null).toString());
        map.put("right", node.jjtGetChild(1).jjtAccept(this, null).toString());
        System.out.println(map);
        return left - right;
    }

    /** 掛け算 */
    public Object visit(ASTMulti node, Object data) {
        Integer left = (Integer) node.jjtGetChild(0).jjtAccept(this, null);
        Integer right = (Integer) node.jjtGetChild(1).jjtAccept(this, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put("logic", "MUL");
        map.put("left", node.jjtGetChild(0).jjtAccept(this, null).toString());
        map.put("right", node.jjtGetChild(1).jjtAccept(this, null).toString());
        System.out.println(map);
        return left * right;
    }

    /**  割り算 */
    public Object visit(ASTDivision node, Object data) {
        Integer left = (Integer) node.jjtGetChild(0).jjtAccept(this, null);
        Integer right = (Integer) node.jjtGetChild(1).jjtAccept(this, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put("logic", "DIV");
        map.put("left", node.jjtGetChild(0).jjtAccept(this, null).toString());
        map.put("right", node.jjtGetChild(1).jjtAccept(this, null).toString());
        System.out.println(map);
        return left / right;
    }

    /** 数値リテラル */
    public Object visit(ASTInteger node, Object data) {
        String value = node.nodeValue;
        return Integer.valueOf(value);
    }
}
package com.golite.ast;

public class LenNode extends Node {
    public Node expr;

    public LenNode(Node expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
package com.golite.ast;

public class StringJoinNode extends Node {

    public Node slice;
    public Node separator;

    public StringJoinNode(Node slice, Node separator, int line, int column) {
        super(line, column);
        this.slice = slice;
        this.separator = separator;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
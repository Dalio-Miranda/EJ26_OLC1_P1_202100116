package com.golite.ast;

public class AppendNode extends Node {
    public Node slice;
    public Node value;

    public AppendNode(Node slice, Node value, int line, int column) {
        super(line, column);
        this.slice = slice;
        this.value = value;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
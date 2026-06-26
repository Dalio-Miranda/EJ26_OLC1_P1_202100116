package com.golite.ast;

public class SliceIndexNode extends Node {
    public Node slice;
    public Node value;

    public SliceIndexNode(Node slice, Node value, int line, int column) {
        super(line, column);
        this.slice = slice;
        this.value = value;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
package com.golite.ast;

public class SliceAccessNode extends Node {
    public Node slice;
    public Node index;

    public SliceAccessNode(Node slice, Node index, int line, int column) {
        super(line, column);
        this.slice = slice;
        this.index = index;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
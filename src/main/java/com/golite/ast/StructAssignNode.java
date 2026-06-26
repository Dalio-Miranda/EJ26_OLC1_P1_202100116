package com.golite.ast;

public class StructAssignNode extends Node {

    public String structName;
    public String fieldName;
    public Node value;

    public StructAssignNode(String structName, String fieldName, Node value,
                            int line, int column) {
        super(line, column);
        this.structName = structName;
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
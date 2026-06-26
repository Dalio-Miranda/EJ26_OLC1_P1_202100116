package com.golite.ast;

public class StructAccessNode extends Node {
    public String structName;
    public String fieldName;

    public StructAccessNode(String structName, String fieldName, int line, int column) {
        super(line, column);
        this.structName = structName;
        this.fieldName = fieldName;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
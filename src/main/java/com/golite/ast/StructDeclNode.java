package com.golite.ast;

import java.util.ArrayList;

public class StructDeclNode extends Node {
    public String name;
    public ArrayList<String[]> fields;

    public StructDeclNode(String name, ArrayList<String[]> fields, int line, int column) {
        super(line, column);
        this.name = name;
        this.fields = fields;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
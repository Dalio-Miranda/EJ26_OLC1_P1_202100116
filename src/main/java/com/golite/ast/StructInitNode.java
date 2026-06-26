package com.golite.ast;

import java.util.ArrayList;

public class StructInitNode extends Node {
    public String structName;
    public ArrayList<String> fieldNames;
    public ArrayList<Node> values;

    public StructInitNode(String structName, ArrayList<String> fieldNames, ArrayList<Node> values, int line, int column) {
        super(line, column);
        this.structName = structName;
        this.fieldNames = fieldNames;
        this.values = values;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
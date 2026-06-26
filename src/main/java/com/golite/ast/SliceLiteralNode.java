package com.golite.ast;

import java.util.ArrayList;

public class SliceLiteralNode extends Node {
    public String elementType;
    public ArrayList<Node> values;

    public SliceLiteralNode(String elementType, ArrayList<Node> values, int line, int column) {
        super(line, column);
        this.elementType = elementType;
        this.values = values;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
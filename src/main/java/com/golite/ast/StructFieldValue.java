package com.golite.ast;

public class StructFieldValue {
    public String name;
    public Node value;

    public StructFieldValue(String name, Node value) {
        this.name = name;
        this.value = value;
    }
}
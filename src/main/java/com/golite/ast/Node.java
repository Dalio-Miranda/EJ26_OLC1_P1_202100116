package com.golite.ast;

public abstract class Node {
    public int line;
    public int column;
    
    public Node(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    public abstract Object accept(Visitor visitor, Environment env);
}
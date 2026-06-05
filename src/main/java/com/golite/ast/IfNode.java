package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de sentencia if-else.
 * Representa la estructura condicional del lenguaje GoLite.
 * Puede tener bloque else opcional.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class IfNode extends Node {

    // Expresion de condicion (debe ser tipo bool)
    public Node condition;

    // Lista de sentencias del bloque if
    public ArrayList<Node> thenStmts;

    // Lista de sentencias del bloque else (null si no hay else)
    public ArrayList<Node> elseStmts;

    /**
     * Constructor
     * @param condition Expresion condicional
     * @param thenStmts Sentencias del bloque if
     * @param elseStmts Sentencias del bloque else (puede ser null)
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public IfNode(Node condition, ArrayList<Node> thenStmts, 
                  ArrayList<Node> elseStmts, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.thenStmts = thenStmts;
        this.elseStmts = elseStmts;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
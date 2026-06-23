package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de un caso individual dentro de un Switch.
 * Contiene la expresion de comparacion y las sentencias a ejecutar.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class CaseNode extends Node {

    // Expresion del case (valor a comparar)
    public Node expression;

    // Lista de sentencias a ejecutar si el case coincide
    public ArrayList<Node> stmts;

    /**
     * Constructor
     * @param expression Expresion del case
     * @param stmts Sentencias del case
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public CaseNode(Node expression, ArrayList<Node> stmts, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.stmts = stmts;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de sentencia Switch-Case.
 * Evalua una expresion y ejecuta el bloque del primer case que coincida.
 * Si ninguno coincide ejecuta el bloque default si existe.
 * En GoLite el break es implicito al final de cada case.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class SwitchNode extends Node {

    // Expresion a evaluar en el switch
    public Node expression;

    // Lista de casos (cada caso es [expresion, listaStmts])
    public ArrayList<CaseNode> cases;

    // Sentencias del bloque default (null si no hay default)
    public ArrayList<Node> defaultStmts;

    /**
     * Constructor
     * @param expression Expresion a evaluar
     * @param cases Lista de casos
     * @param defaultStmts Sentencias del default (puede ser null)
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public SwitchNode(Node expression, ArrayList<CaseNode> cases,
                      ArrayList<Node> defaultStmts, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.cases = cases;
        this.defaultStmts = defaultStmts;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
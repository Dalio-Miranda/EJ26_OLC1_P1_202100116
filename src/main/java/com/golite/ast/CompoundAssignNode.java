package com.golite.ast;

/**
 * Nodo de asignacion compuesta.
 * Representa operaciones de asignacion con operador:
 * += y -=
 * Equivale a: variable = variable operador expresion
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class CompoundAssignNode extends Node {

    // Nombre de la variable
    public String name;

    // Operador compuesto (+= o -=)
    public String operator;

    // Expresion del lado derecho
    public Node value;

    /**
     * Constructor
     * @param name Nombre de la variable
     * @param operator Operador compuesto
     * @param value Expresion del valor
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public CompoundAssignNode(String name, String operator, Node value, 
                               int line, int column) {
        super(line, column);
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
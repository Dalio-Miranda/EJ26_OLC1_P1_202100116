package com.golite.ast;

/**
 * Nodo de incremento o decremento.
 * Representa las operaciones x++ y x--
 * Solo aplica a variables de tipo int o float64.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class IncrDecrNode extends Node {

    // Nombre de la variable
    public String name;

    // Operador (++ o --)
    public String operator;

    /**
     * Constructor
     * @param name Nombre de la variable
     * @param operator Operador (++ o --)
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public IncrDecrNode(String name, String operator, int line, int column) {
        super(line, column);
        this.name = name;
        this.operator = operator;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
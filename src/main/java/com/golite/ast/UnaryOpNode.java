package com.golite.ast;

/**
 * Nodo de operacion unaria.
 * Representa operaciones con un solo operando:
 * - Negacion aritmetica: -expr
 * - Negacion logica: !expr
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class UnaryOpNode extends Node {

    // Operador unario (- o !)
    public String operator;

    // Expresion sobre la que se aplica el operador
    public Node operand;

    /**
     * Constructor
     * @param operator Operador unario
     * @param operand Nodo de la expresion
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public UnaryOpNode(String operator, Node operand, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
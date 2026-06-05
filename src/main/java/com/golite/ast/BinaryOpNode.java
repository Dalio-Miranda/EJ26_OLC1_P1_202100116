package com.golite.ast;

/**
 * Nodo de operación binaria.
 * Representa operaciones aritméticas (+, -, *, /, %),
 * de comparación (==, !=, <, <=, >, >=) y
 * lógicas (&&, ||).
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class BinaryOpNode extends Node {
    
    // Operador de la operación (+, -, *, /, %, ==, !=, <, <=, >, >=, &&, ||)
    public String operator;
    
    // Operando izquierdo de la operación
    public Node left;
    
    // Operando derecho de la operación
    public Node right;

    /**
     * Constructor
     * @param operator Operador de la operación
     * @param left Nodo del operando izquierdo
     * @param right Nodo del operando derecho
     * @param line Línea de la operación
     * @param column Columna de la operación
     */
    public BinaryOpNode(String operator, Node left, Node right, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    /**
     * Acepta un visitor para recorrer el AST
     * @param visitor Visitor que procesará este nodo
     * @param env Ambiente de ejecución actual
     * @return Resultado de la visita
     */
    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
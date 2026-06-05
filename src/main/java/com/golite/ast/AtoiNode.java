package com.golite.ast;

/**
 * Nodo de funcion embebida strconv.Atoi.
 * Convierte una cadena de texto a un valor entero (int).
 * Genera error si la cadena no representa un entero valido.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class AtoiNode extends Node {

    // Expresion de cadena a convertir
    public Node expr;

    /**
     * Constructor
     * @param expr Expresion de tipo string a convertir
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public AtoiNode(Node expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
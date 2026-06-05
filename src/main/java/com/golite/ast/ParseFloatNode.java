package com.golite.ast;

/**
 * Nodo de funcion embebida strconv.ParseFloat.
 * Convierte una cadena de texto a un valor decimal (float64).
 * Genera error si la cadena no representa un numero valido.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class ParseFloatNode extends Node {

    // Expresion de cadena a convertir
    public Node expr;

    /**
     * Constructor
     * @param expr Expresion de tipo string a convertir
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public ParseFloatNode(Node expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
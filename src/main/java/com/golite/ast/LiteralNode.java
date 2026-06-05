package com.golite.ast;

/**
 * Nodo de literal.
 * Representa valores literales del lenguaje como:
 * enteros (42), decimales (3.14), cadenas ("hola"),
 * booleanos (true/false), runes ('A') y nil.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class LiteralNode extends Node {

    // Valor del literal
    public Object value;

    // Tipo del literal (int, float64, string, bool, rune, nil)
    public String type;

    /**
     * Constructor
     * @param value Valor del literal
     * @param type Tipo del literal
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public LiteralNode(Object value, String type, int line, int column) {
        super(line, column);
        this.value = value;
        this.type = type;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
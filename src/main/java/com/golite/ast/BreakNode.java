package com.golite.ast;

/**
 * Nodo de sentencia break.
 * Finaliza inmediatamente el bucle for actual.
 * Solo es valido dentro de un bucle for.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class BreakNode extends Node {

    /**
     * Constructor
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public BreakNode(int line, int column) {
        super(line, column);
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
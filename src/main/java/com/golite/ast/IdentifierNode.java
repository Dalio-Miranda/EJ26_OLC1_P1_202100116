package com.golite.ast;

/**
 * Nodo de identificador.
 * Representa el acceso a una variable por su nombre.
 * Al visitarse busca el valor en el ambiente actual.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class IdentifierNode extends Node {

    // Nombre de la variable
    public String name;

    /**
     * Constructor
     * @param name Nombre del identificador
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public IdentifierNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
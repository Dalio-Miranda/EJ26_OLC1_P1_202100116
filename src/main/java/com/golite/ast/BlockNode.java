package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de bloque de sentencias independiente.
 * Representa un bloque { } que crea su propio ambito
 * sin estar asociado a ninguna estructura de control.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class BlockNode extends Node {

    // Lista de sentencias dentro del bloque
    public ArrayList<Node> statements;

    /**
     * Constructor
     * @param statements Lista de sentencias del bloque
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public BlockNode(ArrayList<Node> statements, int line, int column) {
        super(line, column);
        this.statements = statements;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
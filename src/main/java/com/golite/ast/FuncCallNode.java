package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de llamada a funcion definida por el usuario.
 * Representa la invocacion de una funcion con sus argumentos.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class FuncCallNode extends Node {

    // Nombre de la funcion a llamar
    public String name;

    // Lista de argumentos pasados a la funcion
    public ArrayList<Node> args;

    /**
     * Constructor
     * @param name Nombre de la funcion
     * @param args Lista de argumentos
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public FuncCallNode(String name, ArrayList<Node> args, int line, int column) {
        super(line, column);
        this.name = name;
        this.args = args;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
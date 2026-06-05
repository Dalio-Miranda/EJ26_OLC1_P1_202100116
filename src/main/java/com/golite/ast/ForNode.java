package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de sentencia for.
 * Representa el bucle for del lenguaje GoLite.
 * Soporta dos formas:
 * - for condicion { }
 * - for init; condicion; incremento { }
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class ForNode extends Node {

    // Inicializacion del for (null si es for con condicion simple)
    public Node init;

    // Condicion del bucle (debe ser tipo bool)
    public Node condition;

    // Incremento o decremento (null si es for con condicion simple)
    public Node update;

    // Lista de sentencias del cuerpo del bucle
    public ArrayList<Node> body;

    /**
     * Constructor
     * @param init Nodo de inicializacion (puede ser null)
     * @param condition Nodo de condicion
     * @param update Nodo de actualizacion (puede ser null)
     * @param body Lista de sentencias del cuerpo
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public ForNode(Node init, Node condition, Node update, 
                   ArrayList<Node> body, int line, int column) {
        super(line, column);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
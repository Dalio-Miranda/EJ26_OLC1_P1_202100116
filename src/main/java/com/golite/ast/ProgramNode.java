package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo raíz del AST - Representa el programa completo.
 * Contiene la lista de todas las sentencias del programa.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class ProgramNode extends Node {
    
    // Lista de sentencias del programa
    public ArrayList<Node> statements;

    /**
     * Constructor
     * @param statements Lista de nodos sentencia del programa
     * @param line Línea donde inicia el programa
     * @param column Columna donde inicia el programa
     */
    public ProgramNode(ArrayList<Node> statements, int line, int column) {
        super(line, column);
        this.statements = statements;
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
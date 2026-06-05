package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de funcion embebida fmt.Println.
 * Imprime una o mas expresiones separadas por espacio
 * y agrega un salto de linea al final.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class PrintlnNode extends Node {

    // Lista de expresiones a imprimir
    public ArrayList<Node> args;

    /**
     * Constructor
     * @param args Lista de argumentos a imprimir
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public PrintlnNode(ArrayList<Node> args, int line, int column) {
        super(line, column);
        this.args = args;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
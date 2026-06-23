package com.golite.ast;

/**
 * Nodo de sentencia return.
 * Finaliza la ejecucion de la funcion actual y devuelve un valor.
 * El valor puede ser null si la funcion no retorna nada.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class ReturnNode extends Node {

    // Expresion a retornar (null si no retorna nada)
    public Node value;

    /**
     * Constructor
     * @param value Expresion a retornar (puede ser null)
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public ReturnNode(Node value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
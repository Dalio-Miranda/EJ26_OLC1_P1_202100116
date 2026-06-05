package com.golite.ast;

/**
 * Nodo de funcion embebida reflect.TypeOf.
 * Retorna el tipo de dato de una expresion en tiempo de ejecucion.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class TypeOfNode extends Node {

    // Expresion de la que se quiere obtener el tipo
    public Node expr;

    /**
     * Constructor
     * @param expr Expresion a evaluar
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public TypeOfNode(Node expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
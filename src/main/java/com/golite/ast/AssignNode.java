package com.golite.ast;

/**
 * Nodo de asignación de variable.
 * Representa la asignación de un nuevo valor a una variable
 * ya declarada previamente: x = expr
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class AssignNode extends Node {
    
    // Nombre de la variable a asignar
    public String name;
    
    // Expresión cuyo valor se asignará a la variable
    public Node value;

    /**
     * Constructor
     * @param name Nombre de la variable
     * @param value Nodo expresión del nuevo valor
     * @param line Línea de la asignación
     * @param column Columna de la asignación
     */
    public AssignNode(String name, Node value, int line, int column) {
        super(line, column);
        this.name = name;
        this.value = value;
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
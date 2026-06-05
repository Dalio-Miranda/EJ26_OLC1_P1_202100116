package com.golite.ast;

/**
 * Nodo de declaración de variable.
 * Representa tanto declaraciones explícitas (var x int = 5)
 * como declaraciones implícitas (x := 5).
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class VarDeclNode extends Node {
    
    // Nombre de la variable
    public String name;
    
    // Tipo de dato (int, float64, string, bool, rune, null si es inferido)
    public String type;
    
    // Expresión de valor inicial (puede ser null si no se asigna valor)
    public Node value;
    
    // Indica si es declaración implícita (:=) o explícita (var)
    public boolean isImplicit;

    /**
     * Constructor para declaración explícita con valor: var x int = expr
     * @param name Nombre de la variable
     * @param type Tipo de dato declarado
     * @param value Nodo expresión del valor inicial
     * @param isImplicit true si usa := , false si usa var
     * @param line Línea de la declaración
     * @param column Columna de la declaración
     */
    public VarDeclNode(String name, String type, Node value, boolean isImplicit, int line, int column) {
        super(line, column);
        this.name = name;
        this.type = type;
        this.value = value;
        this.isImplicit = isImplicit;
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
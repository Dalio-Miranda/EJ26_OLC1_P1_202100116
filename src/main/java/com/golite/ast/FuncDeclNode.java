package com.golite.ast;

import java.util.ArrayList;

/**
 * Nodo de declaracion de funcion.
 * Representa la definicion de una funcion con sus parametros
 * y cuerpo. Las funciones solo pueden declararse en ambito global.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class FuncDeclNode extends Node {

    // Nombre de la funcion
    public String name;

    // Lista de parametros: cada elemento es [nombre, tipo]
    public ArrayList<String[]> params;

    // Tipo de retorno (null si no retorna nada)
    public String returnType;

    // Cuerpo de la funcion
    public ArrayList<Node> body;

    /**
     * Constructor
     * @param name Nombre de la funcion
     * @param params Lista de parametros [nombre, tipo]
     * @param returnType Tipo de retorno (null si void)
     * @param body Lista de sentencias del cuerpo
     * @param line Linea donde aparece
     * @param column Columna donde aparece
     */
    public FuncDeclNode(String name, ArrayList<String[]> params,
                        String returnType, ArrayList<Node> body,
                        int line, int column) {
        super(line, column);
        this.name = name;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public Object accept(Visitor visitor, Environment env) {
        return visitor.visit(this, env);
    }
}
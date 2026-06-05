package com.golite.ast;

import java.util.HashMap;

/**
 * Clase Environment - Representa un ámbito (scope) de ejecución.
 * Implementa una tabla de símbolos encadenada donde cada ambiente
 * tiene referencia a su ambiente padre, permitiendo el acceso
 * a variables de ámbitos superiores.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class Environment {
    
    // Mapa de variables: nombre -> [tipo, valor]
    private HashMap<String, Object[]> variables;
    
    // Referencia al ambiente padre (null si es global)
    private Environment parent;

    /**
     * Constructor - Crea un nuevo ambiente con referencia al padre
     * @param parent Ambiente padre, null si es el ámbito global
     */
    public Environment(Environment parent) {
        this.variables = new HashMap<>();
        this.parent = parent;
    }

    /**
     * Declara una nueva variable en el ámbito actual
     * @param name Nombre de la variable
     * @param type Tipo de dato (int, float64, string, bool, rune)
     * @param value Valor inicial de la variable
     */
    public void declare(String name, String type, Object value) {
        variables.put(name, new Object[]{type, value});
    }

    /**
     * Verifica si una variable existe únicamente en el ámbito local
     * @param name Nombre de la variable
     * @return true si existe en este ámbito, false en caso contrario
     */
    public boolean existsLocal(String name) {
        return variables.containsKey(name);
    }

    /**
     * Verifica si una variable existe en este ámbito o en alguno superior
     * @param name Nombre de la variable
     * @return true si existe en algún ámbito, false en caso contrario
     */
    public boolean exists(String name) {
        if (variables.containsKey(name)) return true;
        if (parent != null) return parent.exists(name);
        return false;
    }

    /**
     * Obtiene el valor de una variable buscando en la cadena de ámbitos
     * @param name Nombre de la variable
     * @return Valor de la variable o null si no existe
     */
    public Object getValue(String name) {
        if (variables.containsKey(name)) return variables.get(name)[1];
        if (parent != null) return parent.getValue(name);
        return null;
    }

    /**
     * Obtiene el tipo de una variable buscando en la cadena de ámbitos
     * @param name Nombre de la variable
     * @return Tipo de la variable como String o null si no existe
     */
    public String getType(String name) {
        if (variables.containsKey(name)) return (String) variables.get(name)[0];
        if (parent != null) return parent.getType(name);
        return null;
    }

    /**
     * Actualiza el valor de una variable existente en la cadena de ámbitos
     * @param name Nombre de la variable
     * @param value Nuevo valor a asignar
     */
    public void setValue(String name, Object value) {
        if (variables.containsKey(name)) {
            variables.get(name)[1] = value;
        } else if (parent != null) {
            parent.setValue(name, value);
        }
    }

    /**
     * Retorna el mapa completo de variables del ámbito actual
     * @return HashMap con todas las variables del ámbito
     */
    public HashMap<String, Object[]> getVariables() {
        return variables;
    }

    /**
     * Retorna el ambiente padre
     * @return Ambiente padre o null si es global
     */
    public Environment getParent() {
        return parent;
    }
}
package com.golite;

import analisis.Scanner;
import analisis.parser;
import com.golite.ast.*;
import com.golite.interpreter.Interpreter;
import java.io.BufferedReader;
import java.io.StringReader;
import java_cup.runtime.Symbol;

/**
 * Clase principal del interprete GoLite.
 * Punto de entrada de la aplicacion.
 * Coordina el analisis lexico, sintactico y semantico
 * del codigo fuente GoLite.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class Main {

    /**
     * Metodo principal de la aplicacion.
     * Por ahora ejecuta un codigo de prueba hardcodeado.
     * Luego sera reemplazado por la GUI.
     * @param args Argumentos de linea de comandos (no se usan)
     */
    public static void main(String[] args) {
        /* Codigo de prueba basico en GoLite */
        String codigo = """
                    var x int = 10
                    var y int = 20
                    z := x + y
                    fmt.Println("Suma:", z)
                    
                    if z > 25 {
                        fmt.Println("z es mayor que 25")
                    } else {
                        fmt.Println("z es menor o igual a 25")
                    }
                    
                    i := 1
                    for i <= 3 {
                        fmt.Println("i =", i)
                        i++
                }
                """;

        /* Ejecutar el interprete con el codigo de prueba */
        String resultado = interpretar(codigo);
        System.out.println("=== SALIDA ===");
        System.out.println(resultado);
    }

    /**
     * Interpreta un codigo fuente GoLite.
     * Realiza el analisis lexico, sintactico y semantico.
     * @param codigo Codigo fuente en GoLite
     * @return String con la salida de la consola
     */
    public static String interpretar(String codigo) {
    try {
        /* Limpiar lista de tokens del analisis anterior */
        Scanner.listaTokens.clear();

        /* Crear el scanner (analizador lexico) */
        Scanner scanner = new Scanner(
            new BufferedReader(new StringReader(codigo))
        );

        /* Crear el parser (analizador sintactico) */
        parser p = new parser(scanner);

        /* Ejecutar el analisis y obtener el AST */
        java_cup.runtime.Symbol resultado = p.parse();
        ProgramNode ast = (ProgramNode) resultado.value;

        if (ast == null) {
            return "Error: no se pudo generar el AST";
        }

        /* Crear el interprete y ejecutar el AST */
        Interpreter interprete = new Interpreter();
        Environment env = new Environment(null);
        ast.accept(interprete, env);

        /* Retornar la salida de la consola */
        return interprete.consola.toString();

    } catch (Exception e) {
        return "Error durante la interpretacion: " + e.getMessage();
    }
}
}
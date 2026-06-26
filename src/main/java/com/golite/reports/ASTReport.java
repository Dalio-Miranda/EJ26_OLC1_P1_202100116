package com.golite.reports;

import com.golite.ast.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

public class ASTReport {
    private int contador = 0;
    private StringBuilder dot = new StringBuilder();

    public String generar(Node raiz) {
        contador = 0;
        dot = new StringBuilder();

        dot.append("digraph AST {\n");
        dot.append("rankdir=TB;\n");
        dot.append("node [shape=box, style=filled, color=\"#1f77b4\", fillcolor=\"#dbeafe\"];\n");
        dot.append("edge [color=\"#555555\"];\n");

        recorrer(raiz);

        dot.append("}\n");
        return dot.toString();
    }

   public File generarImagen(Node raiz) throws Exception {
    String dotTexto = generar(raiz);

    String rutaBase = System.getProperty("user.dir");
    File carpeta = new File(rutaBase, "reportes");

    if (!carpeta.exists()) {
        carpeta.mkdirs();
    }

    File dotFile = new File(carpeta, "ast.dot");
    File pngFile = new File(carpeta, "ast.png");

    System.out.println("Ruta base: " + rutaBase);
    System.out.println("Generando DOT en: " + dotFile.getAbsolutePath());
    System.out.println("Generando PNG en: " + pngFile.getAbsolutePath());

    try (FileWriter writer = new FileWriter(dotFile)) {
        writer.write(dotTexto);
    }

    ProcessBuilder pb = new ProcessBuilder(
        "dot",
        "-Tpng",
        dotFile.getAbsolutePath(),
        "-o",
        pngFile.getAbsolutePath()
    );

    pb.redirectErrorStream(true);
    Process proceso = pb.start();

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(proceso.getInputStream())
    );

    String linea;
    StringBuilder salidaGraphviz = new StringBuilder();

    while ((linea = reader.readLine()) != null) {
        salidaGraphviz.append(linea).append("\n");
    }

    int salida = proceso.waitFor();

    if (salida != 0) {
        throw new RuntimeException(
            "Graphviz no pudo generar la imagen AST.\n" + salidaGraphviz.toString()
        );
    }

    if (!pngFile.exists()) {
        throw new RuntimeException("No se encontró el archivo PNG generado: " + pngFile.getAbsolutePath());
    }

    return pngFile;
}

    private int recorrer(Object obj) {
        if (obj == null) {
            int id = contador++;
            dot.append("n").append(id).append(" [label=\"nil\"];\n");
            return id;
        }

        int idActual = contador++;
        String nombre = obj.getClass().getSimpleName();
        dot.append("n").append(idActual).append(" [label=\"")
           .append(escape(nombre)).append("\"];\n");

        if (!(obj instanceof Node)) {
            return idActual;
        }

        Field[] campos = obj.getClass().getFields();

        for (Field campo : campos) {
            try {
                Object valor = campo.get(obj);

                if (valor == null) continue;

                if (valor instanceof Node) {
                    int idHijo = recorrer(valor);
                    dot.append("n").append(idActual).append(" -> n").append(idHijo)
                       .append(" [label=\"").append(campo.getName()).append("\"];\n");

                } else if (valor instanceof ArrayList<?>) {
                    int idLista = contador++;
                    dot.append("n").append(idLista).append(" [label=\"")
                       .append(campo.getName()).append("\"];\n");
                    dot.append("n").append(idActual).append(" -> n").append(idLista).append(";\n");

                    for (Object item : (ArrayList<?>) valor) {
                        if (item instanceof Node) {
                            int idItem = recorrer(item);
                            dot.append("n").append(idLista).append(" -> n").append(idItem).append(";\n");
                        }
                    }

                } else if (valor instanceof String || valor instanceof Number
                        || valor instanceof Boolean || valor instanceof Character) {
                    int idValor = contador++;
                    dot.append("n").append(idValor).append(" [label=\"")
                       .append(campo.getName()).append(": ")
                       .append(escape(valor.toString())).append("\"];\n");
                    dot.append("n").append(idActual).append(" -> n").append(idValor).append(";\n");
                }

            } catch (Exception e) {
                // Ignorar campos que no se puedan leer
            }
        }

        return idActual;
    }

    private String escape(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
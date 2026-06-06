package com.golite;

import com.golite.gui.MainWindow;
import javax.swing.SwingUtilities;

/**
 * Clase principal del interprete GoLite.
 * Lanza la interfaz grafica del IDE.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class Main {

    /**
     * Punto de entrada de la aplicacion
     * @param args Argumentos de linea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
}
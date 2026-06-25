package com.golite.gui;

import analisis.Scanner;
import analisis.parser;
import com.golite.ast.*;
import com.golite.interpreter.Interpreter;
import com.golite.reports.ASTReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java_cup.runtime.Symbol;
import java.util.ArrayList;
import java.io.File;

/**
 * Ventana principal del IDE para el lenguaje GoLite.
 * Implementa un editor de texto con soporte para:
 * - Crear, abrir y guardar archivos .glt
 * - Ejecutar codigo GoLite
 * - Ver reportes de errores y tokens
 * - Consola de salida
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class MainWindow extends JFrame {

    /* ===== COMPONENTES DE LA INTERFAZ ===== */

    // Area de edicion de codigo
    private JTextArea editorArea;

    // Area de consola de salida
    private JTextArea consolaArea;

    // Etiqueta que muestra la linea actual del cursor
    private JLabel lblLinea;

    // Archivo actualmente abierto
    private File archivoActual;

    /* ===== CONSTRUCTOR ===== */

    /**
     * Constructor - Inicializa y muestra la ventana principal
     */
    public MainWindow() {
        initComponents();
        setVisible(true);
    }

    /* ===== INICIALIZACION DE COMPONENTES ===== */

    /**
     * Inicializa todos los componentes de la interfaz grafica
     */
    private void initComponents() {
        /* Configuracion de la ventana principal */
        setTitle("GoLite IDE - 202100116");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        /* Aplicar tema oscuro */
        getContentPane().setBackground(new Color(30, 30, 30));

        /* Crear menu */
        setJMenuBar(crearMenuBar());

        /* Panel principal dividido en editor y consola */
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setBackground(new Color(30, 30, 30));

        /* Panel superior: editor de codigo */
        splitPane.setTopComponent(crearPanelEditor());

        /* Panel inferior: consola de salida */
        splitPane.setBottomComponent(crearPanelConsola());

        add(splitPane, BorderLayout.CENTER);

        /* Barra de estado inferior */
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    /**
     * Crea la barra de menu con todas las opciones
     * @return JMenuBar configurado
     */
    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(45, 45, 45));
        menuBar.setForeground(Color.WHITE);

        /* Menu Archivo */
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setForeground(Color.WHITE);

        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemGuardarComo = new JMenuItem("Guardar Como");

        /* Estilo de los items */
        Color bgItem = new Color(45, 45, 45);
        itemNuevo.setBackground(bgItem);
        itemNuevo.setForeground(Color.WHITE);
        itemAbrir.setBackground(bgItem);
        itemAbrir.setForeground(Color.WHITE);
        itemGuardar.setBackground(bgItem);
        itemGuardar.setForeground(Color.WHITE);
        itemGuardarComo.setBackground(bgItem);
        itemGuardarComo.setForeground(Color.WHITE);

        /* Acciones del menu Archivo */
        itemNuevo.addActionListener(e -> nuevoArchivo());
        itemAbrir.addActionListener(e -> abrirArchivo());
        itemGuardar.addActionListener(e -> guardarArchivo());
        itemGuardarComo.addActionListener(e -> guardarArchivoComo());

        /* Atajos de teclado */
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        itemAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);

        /* Menu Ejecutar */
        JMenu menuEjecutar = new JMenu("Ejecutar");
        menuEjecutar.setForeground(Color.WHITE);

        JMenuItem itemEjecutar = new JMenuItem("▶ Ejecutar");
        itemEjecutar.setBackground(bgItem);
        itemEjecutar.setForeground(new Color(100, 220, 100));
        itemEjecutar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        itemEjecutar.addActionListener(e -> ejecutar());

        menuEjecutar.add(itemEjecutar);

        /* Menu Reportes */
        JMenu menuReportes = new JMenu("Reportes");
        menuReportes.setForeground(Color.WHITE);

        JMenuItem itemTokens = new JMenuItem("Tabla de Tokens");
        JMenuItem itemErrores = new JMenuItem("Reporte de Errores");
        JMenuItem itemAST = new JMenuItem("Reporte AST");

        itemTokens.setBackground(bgItem);
        itemTokens.setForeground(Color.WHITE);
        itemErrores.setBackground(bgItem);
        itemErrores.setForeground(Color.WHITE);
        itemAST.setBackground(bgItem);
        itemAST.setForeground(Color.WHITE);

        itemTokens.addActionListener(e -> mostrarTokens());
        itemErrores.addActionListener(e -> mostrarErrores());
        itemAST.addActionListener(e -> mostrarAST());

        menuReportes.add(itemTokens);
        menuReportes.add(itemErrores);
        menuReportes.add(itemAST);

        menuBar.add(menuArchivo);
        menuBar.add(menuEjecutar);
        menuBar.add(menuReportes);

        return menuBar;
    }

    /**
     * Crea el panel del editor de codigo con numeros de linea
     * @return JPanel con el editor configurado
     */
    private JPanel crearPanelEditor() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        /* Etiqueta del panel */
        JLabel lblEditor = new JLabel("  Editor GoLite");
        lblEditor.setForeground(new Color(150, 150, 150));
        lblEditor.setBackground(new Color(40, 40, 40));
        lblEditor.setOpaque(true);
        lblEditor.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
        panel.add(lblEditor, BorderLayout.NORTH);

        /* Area de edicion */
        editorArea = new JTextArea();
        editorArea.setBackground(new Color(30, 30, 30));
        editorArea.setForeground(new Color(220, 220, 220));
        editorArea.setCaretColor(Color.WHITE);
        editorArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        editorArea.setTabSize(4);
        editorArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        /* Codigo de ejemplo al iniciar */
        editorArea.setText(
            "// Programa de prueba GoLite\n" +
            "var x int = 10\n" +
            "var y int = 20\n" +
            "z := x + y\n" +
            "fmt.Println(\"Suma:\", z)\n\n" +
            "if z > 25 {\n" +
            "    fmt.Println(\"z es mayor que 25\")\n" +
            "} else {\n" +
            "    fmt.Println(\"z es menor o igual a 25\")\n" +
            "}\n\n" +
            "i := 1\n" +
            "for i <= 3 {\n" +
            "    fmt.Println(\"i =\", i)\n" +
            "    i++\n" +
            "}"
        );

        /* Listener para actualizar numero de linea */
        editorArea.addCaretListener(e -> actualizarLinea());

        /* Numeros de linea */
        TextLineNumber lineNumbers = new TextLineNumber(editorArea);
        lineNumbers.setBackground(new Color(40, 40, 40));
        lineNumbers.setForeground(new Color(100, 100, 100));
        lineNumbers.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollEditor = new JScrollPane(editorArea);
        scrollEditor.setRowHeaderView(lineNumbers);
        scrollEditor.setBorder(BorderFactory.createEmptyBorder());
        scrollEditor.getViewport().setBackground(new Color(30, 30, 30));

        panel.add(scrollEditor, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel de consola de salida
     * @return JPanel con la consola configurada
     */
    private JPanel crearPanelConsola() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 25));

        /* Encabezado de la consola */
        JPanel headerConsola = new JPanel(new BorderLayout());
        headerConsola.setBackground(new Color(40, 40, 40));

        JLabel lblConsola = new JLabel("  Consola");
        lblConsola.setForeground(new Color(150, 150, 150));
        lblConsola.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
        headerConsola.add(lblConsola, BorderLayout.WEST);

        /* Boton para limpiar consola */
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(60, 60, 60));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> consolaArea.setText(""));
        headerConsola.add(btnLimpiar, BorderLayout.EAST);

        panel.add(headerConsola, BorderLayout.NORTH);

        /* Area de consola */
        consolaArea = new JTextArea();
        consolaArea.setBackground(new Color(25, 25, 25));
        consolaArea.setForeground(new Color(200, 200, 200));
        consolaArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        consolaArea.setEditable(false);
        consolaArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        JScrollPane scrollConsola = new JScrollPane(consolaArea);
        scrollConsola.setBorder(BorderFactory.createEmptyBorder());
        scrollConsola.getViewport().setBackground(new Color(25, 25, 25));

        panel.add(scrollConsola, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea la barra de estado inferior
     * @return JPanel con la barra de estado
     */
    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(0, 120, 215));

        lblLinea = new JLabel("Linea: 1 | Columna: 1");
        lblLinea.setForeground(Color.WHITE);
        lblLinea.setFont(new Font("Consolas", Font.PLAIN, 12));

        panel.add(lblLinea);
        return panel;
    }

    /* ===== ACCIONES DEL MENU ===== */

    /**
     * Crea un nuevo archivo en blanco
     */
    private void nuevoArchivo() {
        editorArea.setText("");
        archivoActual = null;
        setTitle("GoLite IDE - Nuevo archivo");
        consolaArea.setText("");
    }

    /**
     * Abre un archivo .glt desde el disco
     */
    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos GoLite (*.glt)", "glt"
            )
        );

        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = chooser.getSelectedFile();
            try {
                String contenido = new String(
                    Files.readAllBytes(archivoActual.toPath())
                );
                editorArea.setText(contenido);
                setTitle("GoLite IDE - " + archivoActual.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al abrir el archivo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Guarda el archivo actual en disco
     */
    private void guardarArchivo() {
        if (archivoActual == null) {
            guardarArchivoComo();
        } else {
            try {
                Files.write(archivoActual.toPath(),
                    editorArea.getText().getBytes());
                setTitle("GoLite IDE - " + archivoActual.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Guarda el archivo con un nuevo nombre
     */
    private void guardarArchivoComo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos GoLite (*.glt)", "glt"
            )
        );

        int resultado = chooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = chooser.getSelectedFile();
            /* Agregar extension .glt si no la tiene */
            if (!archivoActual.getName().endsWith(".glt")) {
                archivoActual = new File(archivoActual.getPath() + ".glt");
            }
            guardarArchivo();
        }
    }

    /**
     * Ejecuta el codigo GoLite del editor
     */
    private void ejecutar() {
        consolaArea.setText("");
        String codigo = editorArea.getText();

        consolaArea.append("=== Ejecutando GoLite ===\n\n");

        try {
            /* Limpiar tokens anteriores */
            Scanner.listaTokens.clear();

            /* Crear scanner y parser */
            Scanner scanner = new Scanner(
                new BufferedReader(new StringReader(codigo))
            );
            parser p = new parser(scanner);

            /* Parsear y obtener AST */
            Symbol resultado = p.parse();
            ProgramNode ast = (ProgramNode) resultado.value;

            if (ast == null) {
                consolaArea.append("[ERROR] No se pudo generar el AST\n");
                return;
            }

            /* Ejecutar el interprete */
            Interpreter interprete = new Interpreter();
            Environment env = new Environment(null);
            ast.accept(interprete, env);

            /* Mostrar salida en consola */
            if (!interprete.consola.toString().isEmpty()) {
                consolaArea.append(interprete.consola.toString());
            }

            /* Mostrar errores semanticos si los hay */
            if (!interprete.errores.isEmpty()) {
                consolaArea.append("\n=== Errores Semanticos ===\n");
                for (String[] error : interprete.errores) {
                    consolaArea.append("[" + error[3] + "] " + error[0]
                        + " (linea " + error[1] + ", columna " + error[2] + ")\n");
                }
            }

            /* Mostrar errores sintacticos si los hay */
            if (!p.errores.isEmpty()) {
                consolaArea.append("\n=== Errores Sintacticos ===\n");
                for (String[] error : p.errores) {
                    consolaArea.append("[" + error[3] + "] " + error[0]
                        + " (linea " + error[1] + ", columna " + error[2] + ")\n");
                }
            }

            consolaArea.append("\n=== Ejecucion completada ===\n");

        } catch (Exception e) {
            consolaArea.append("[ERROR] " + e.getMessage() + "\n");
        }
    }

    /**
     * Muestra la tabla de tokens en una ventana nueva
     */
    private void mostrarTokens() {
        /* Columnas de la tabla */
        String[] columnas = {"No.", "Lexema", "Tipo", "Linea", "Columna"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        /* Agregar tokens reconocidos */
        int no = 1;
        for (String[] token : Scanner.listaTokens) {
            modelo.addRow(new Object[]{
                no++, token[0], token[1], token[2], token[3]
            });
        }

        /* Crear tabla */
        JTable tabla = new JTable(modelo);
        tabla.setBackground(new Color(40, 40, 40));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(new Color(70, 70, 70));
        tabla.getTableHeader().setBackground(new Color(0, 120, 215));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setFont(new Font("Consolas", Font.PLAIN, 13));
        tabla.setRowHeight(22);

        /* Mostrar en ventana nueva */
        JFrame ventana = new JFrame("Tabla de Tokens");
        ventana.setSize(700, 500);
        ventana.setLocationRelativeTo(this);
        ventana.add(new JScrollPane(tabla));
        ventana.setVisible(true);
    }

    /**
     * Muestra el reporte de errores en una ventana nueva
     */
    private void mostrarErrores() {
        /* Columnas de la tabla */
        String[] columnas = {"No.", "Descripcion", "Linea", "Columna", "Tipo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        /* Intentar ejecutar para obtener errores */
        try {
            Scanner.listaTokens.clear();
            Scanner scanner = new Scanner(
                new BufferedReader(new StringReader(editorArea.getText()))
            );
            parser p = new parser(scanner);
            Symbol resultado = p.parse();

            /* Errores sintacticos */
            int no = 1;
            for (String[] error : p.errores) {
                modelo.addRow(new Object[]{
                    no++, error[0], error[1], error[2], error[3]
                });
            }

            /* Errores semanticos */
            if (resultado != null && resultado.value != null) {
                Interpreter interprete = new Interpreter();
                Environment env = new Environment(null);
                ((ProgramNode) resultado.value).accept(interprete, env);
                for (String[] error : interprete.errores) {
                    modelo.addRow(new Object[]{
                        no++, error[0], error[1], error[2], error[3]
                    });
                }
            }
        } catch (Exception e) {
            modelo.addRow(new Object[]{1, e.getMessage(), "-", "-", "Fatal"});
        }

        /* Crear tabla */
        JTable tabla = new JTable(modelo);
        tabla.setBackground(new Color(40, 40, 40));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(new Color(70, 70, 70));
        tabla.getTableHeader().setBackground(new Color(200, 50, 50));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setFont(new Font("Consolas", Font.PLAIN, 13));
        tabla.setRowHeight(22);

        /* Mostrar en ventana nueva */
        JFrame ventana = new JFrame("Reporte de Errores");
        ventana.setSize(800, 400);
        ventana.setLocationRelativeTo(this);
        ventana.add(new JScrollPane(tabla));
        ventana.setVisible(true);
    }
private void mostrarAST() {
    try {
        Scanner.listaTokens.clear();

        Scanner scanner = new Scanner(
            new BufferedReader(new StringReader(editorArea.getText()))
        );

        parser p = new parser(scanner);
        Symbol resultado = p.parse();

        if (resultado == null || resultado.value == null) {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo generar el AST.",
                "AST",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        ProgramNode ast = (ProgramNode) resultado.value;

        ASTReport reporte = new ASTReport();
        File imagenAST = reporte.generarImagen(ast);

        JFrame ventana = new JFrame("Reporte AST");
        ventana.setSize(1200, 700);
        ventana.setLocationRelativeTo(this);

        ImageIcon icono = new ImageIcon(imagenAST.getAbsolutePath());
        JLabel lblImagen = new JLabel(icono);

        JScrollPane scroll = new JScrollPane(lblImagen);

        ventana.add(scroll);
        ventana.setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            this,
            "Error al generar AST: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        e.printStackTrace();
    }
}

    
    /**
     * Actualiza el indicador de linea y columna en la barra de estado
     */
    private void actualizarLinea() {
        try {
            int pos = editorArea.getCaretPosition();
            int linea = editorArea.getLineOfOffset(pos) + 1;
            int columna = pos - editorArea.getLineStartOffset(linea - 1) + 1;
            lblLinea.setText("Linea: " + linea + " | Columna: " + columna);
        } catch (Exception e) {
            lblLinea.setText("Linea: 1 | Columna: 1");
        }
    }

    /* ===== METODO PRINCIPAL ===== */

    /**
     * Punto de entrada de la aplicacion GUI
     * @param args Argumentos de linea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                // Usar look and feel por defecto
            }
            new MainWindow();
        });
    }
}
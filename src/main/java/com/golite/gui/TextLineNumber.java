package com.golite.gui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.beans.*;

/**
 * Componente que muestra los numeros de linea para un JTextArea.
 * Se coloca como rowHeader de un JScrollPane.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class TextLineNumber extends JPanel
    implements CaretListener, DocumentListener, PropertyChangeListener {

    // Referencia al area de texto
    private JTextArea textArea;

    // Ancho minimo del panel de numeros
    private static final int MINIMUM_DISPLAY_DIGITS = 3;

    // Color del numero de linea actual
    private Color currentLineForeground = new Color(200, 200, 200);

    /**
     * Constructor
     * @param textArea Area de texto a la que se asocian los numeros
     */
    public TextLineNumber(JTextArea textArea) {
        this.textArea = textArea;
        setFont(textArea.getFont());
        setBackground(new Color(40, 40, 40));
        setForeground(new Color(100, 100, 100));
        setPreferredSize();

        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);
        textArea.addPropertyChangeListener("font", this);
    }

    /**
     * Ajusta el tamano preferido segun el numero de digitos necesarios
     */
    private void setPreferredSize() {
        int lines = textArea.getLineCount();
        int digits = Math.max(
            String.valueOf(lines).length(), MINIMUM_DISPLAY_DIGITS
        );
        FontMetrics fm = getFontMetrics(getFont());
        int width = fm.charWidth('0') * digits + 10;
        setPreferredSize(new Dimension(width, 0));
    }

    /**
     * Pinta los numeros de linea
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();

        /* Obtener linea actual del cursor */
        int currentLine = 0;
        try {
            int pos = textArea.getCaretPosition();
            currentLine = textArea.getLineOfOffset(pos);
        } catch (Exception e) {
            // Ignorar
        }

        /* Dibujar cada numero de linea */
        int lineCount = textArea.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            String lineNum = String.valueOf(i + 1);
            int x = getWidth() - fm.stringWidth(lineNum) - 5;
            int y = (i + 1) * lineHeight - (lineHeight - fm.getAscent()) / 2 - 2;

            /* Resaltar linea actual */
            if (i == currentLine) {
                g.setColor(currentLineForeground);
            } else {
                g.setColor(getForeground());
            }
            g.drawString(lineNum, x, y);
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        repaint();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        setPreferredSize();
        repaint();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        setPreferredSize();
        repaint();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        setPreferredSize();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getNewValue() instanceof Font) {
            setFont((Font) e.getNewValue());
            setPreferredSize();
            repaint();
        }
    }
}
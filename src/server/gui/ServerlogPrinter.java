package server.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static server.gui.LogPanel.*;
import static server.gui.ServerGUI.*;

public class ServerlogPrinter {

    private final JTextPane textPane;
    private static ServerlogPrinter instance;

    private ServerlogPrinter(JTextPane textPane) {
        this.textPane = textPane;
        textPane.setBounds(textPane.getParent().getBounds());
    }

    public static ServerlogPrinter getInstance(JTextPane textPane) {
        if (instance == null) {
            instance = new ServerlogPrinter(textPane);
        }
        return instance;
    }

    public void printErrors(String error) {
        printMessageWithStyle(error, errorStyle);
    }

    public void printLogs(String message) {
        printMessageWithStyle(message, logStyle);
    }

    private void printMessageWithStyle(String message, Style style) {
        if (textPane == null) {
            System.err.println("TextPane is null");
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd [HH:mm:ss]"));
        try {
            styledDoc.insertString(styledDoc.getLength(), timestamp + " ", timeStyle);
            styledDoc.insertString(styledDoc.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        limitLineCount(textPane.getHeight() / ((int)OCRA12.getSize2D() + 3));
    }
    private void limitLineCount(int limit) {
        try {
            int totalLines = textPane.getDocument().getDefaultRootElement().getElementCount();
            if (totalLines > limit) {
                int end = textPane.getDocument().getDefaultRootElement().getElement(0).getEndOffset();
                styledDoc.remove(0, end);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}

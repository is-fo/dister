package server.gui.logs;

import server.gui.MatrixBackdrop;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ComponentAdapter;

import static server.gui.ServerGUI.*;

public class LogPanel extends JPanel {

    private JTextPane textPane;
    public static StyledDocument styledDoc;
    public static Style logStyle, errorStyle, timeStyle;

    private final ServerlogPrinter serverlogPrinter;

    public ServerlogPrinter getServerlogPrinter() {
        return serverlogPrinter;
    }

    public LogPanel() {
        setOpaque(false);
        setLayout(null);
        textPane = createTextPane();
        initStyles();
        add(textPane);
        styledDoc = textPane.getStyledDocument();
        serverlogPrinter = ServerlogPrinter.getInstance(textPane);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                for (Component c : getComponents()) {
                    c.setBounds(getBounds());
                }
            }
        });

        new MatrixBackdrop(this).startMatrixText();
    }

    private JTextPane createTextPane() {
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setOpaque(false);

        return textPane;
    }

    private void initStyles() {
        styledDoc = textPane.getStyledDocument();

        logStyle = styledDoc.addStyle("log", null);
        StyleConstants.setForeground(logStyle, HACKERTEXT);
        StyleConstants.setFontFamily(logStyle, OCRA12.getFamily());

        errorStyle = styledDoc.addStyle("error", null);
        StyleConstants.setForeground(errorStyle, CLOSE);
        StyleConstants.setFontFamily(errorStyle, OCRA12.getFamily());

        timeStyle = styledDoc.addStyle("time", null);
        StyleConstants.setForeground(timeStyle, TIMESTAMP);
        StyleConstants.setFontFamily(timeStyle, OCRA12.getFamily());

    }
}

package server.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.io.File;
import java.io.IOException;

public class ServerGUI {

    public static final Color TITLEBAR = new Color(0x0D0D0D);
    public static final Color TITLETEXT = new Color(0x39FF14);
    public static final Color BLACK = new Color(0x000000);
    public static final Color BACKDROP = new Color(0x003300);
    public static final Color HACKERTEXT = new Color(0x00FF00);
    public static final Color TIMESTAMP = new Color(0x07AB1F);

    public static final Color CLOSE = new Color(200, 22, 22);
    public static final Color MINIMIZE = new Color(0x005F00);
    public static final Color INACTIVE = new Color(0x1A1A1A);

    protected static Font OCRA12;
    protected static Font MATRIX16;

    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;

    private static ServerGUI serverGUI;
    private LogPanel logPanel;

    private ServerGUI() {
        initFonts();
        createWindow();
    }

    public static ServerGUI getInstance() {
        if (serverGUI == null) {
            serverGUI = new ServerGUI();
        }

        return serverGUI;
    }

    public ServerlogPrinter getServerlogPrinter() {
        return logPanel.getServerlogPrinter();
    }

    private void initFonts() {
        final String FONT_PATH = "src/model/fonts/";
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            OCRA12 = Font.createFont(0, new File(FONT_PATH + "OCR-A.ttf")).deriveFont(12f);
            ge.registerFont(OCRA12);
            MATRIX16 = Font.createFont(0, new File(FONT_PATH + "matrix.ttf")).deriveFont(18f);
            ge.registerFont(MATRIX16);
        } catch (FontFormatException e) {
            System.err.println("FontFormatException: " + e);
            System.exit(0);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
            System.exit(0);
        }
    }

    private void createWindow() {
        JFrame window = new JFrame("dister");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.setSize(WIDTH, HEIGHT - 1);
        window.setMinimumSize(new Dimension(200, 200));
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        JPanel mainPanel = new JPanel();
        window.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(null);
        mainPanel.setBackground(BLACK);
        mainPanel.setPreferredSize(window.getSize());

        mainPanel.add(logPanel = new LogPanel());
        logPanel.setSize(logPanel.getParent().getSize());

        for (Component c : window.getContentPane().getComponents()) {
            c.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    for (Component c : mainPanel.getComponents()) {
                        c.setBounds(mainPanel.getBounds());
                    }
                }
            });
        }
    }
}

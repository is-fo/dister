package server.gui;

import server.ChatServer;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
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

    public static final Font ICON = new Font("Consolas", Font.BOLD, 32);
    public static final Font sICON = new Font("Consolas", Font.PLAIN, 28);

    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;
    public static final int BUTTON_DIM = 32;

    private JFrame window;
    private JPanel mainPanel;
    private JTextPane textPane;
    public static StyledDocument styledDoc;
    public static Style logStyle, errorStyle, timeStyle;

    private static ServerGUI serverGUI;
    private ChatServer chatServer;
    private ServerlogPrinter serverlogPrinter;

    private ServerGUI(ChatServer chatServer) {
        this.chatServer = chatServer;
        initFonts();
        createWindow();
    }

    public static ServerGUI getInstance(ChatServer chatServer) {
        if (serverGUI == null) {
            serverGUI = new ServerGUI(chatServer);
        }

        return serverGUI;
    }

    public ServerlogPrinter getServerlogPrinter() {
        return serverlogPrinter;
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

    private void createWindow() {
        window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLayout(null);
        window.setSize(WIDTH, HEIGHT);
        window.setLocationRelativeTo(null);
        window.setUndecorated(true);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, WIDTH, 800);
        mainPanel.setBackground(BLACK);
        mainPanel.setVisible(true);
        mainPanel.add(createCustomTitleBar());

        textPane = createTextPane();
        styledDoc = textPane.getStyledDocument();
        serverlogPrinter = ServerlogPrinter.getInstance(textPane);
        mainPanel.add(textPane);

        new MatrixBackdrop().startMatrixText(mainPanel);

        window.add(mainPanel);
        window.setVisible(true);
    }

    private JTextPane createTextPane() {
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(window.getBackground());
        textPane.setVisible(true);
        textPane.setBounds(0, BUTTON_DIM, WIDTH, mainPanel.getHeight() - BUTTON_DIM);
        initStyles();
        textPane.setOpaque(false);

        return textPane;
    }


    //TODO clean up this mess XD
    private JPanel createCustomTitleBar() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(null);
        titlePanel.setBounds(0, 0, WIDTH, BUTTON_DIM);
        titlePanel.setBackground(TITLEBAR);

        JLabel title = new JLabel("pisscord | PISSCORD j SCUFFED SERVERLOGS");
        title.setFont(MATRIX16);
        title.setForeground(TITLETEXT);
        title.setOpaque(false);
        title.setBounds(6, 2, title.getText().length() * 16, 32);
        titlePanel.add(title);

        JPanel buttonPanel = new TitleBar(window);
        buttonPanel.setLayout(null);
        buttonPanel.setBounds(WIDTH - (BUTTON_DIM * 2), 0, ( 2 * BUTTON_DIM), BUTTON_DIM);

        new Draggable(window).enableDraggable(titlePanel);

        buttonPanel.setOpaque(false);
        titlePanel.add(buttonPanel);
        titlePanel.setVisible(true);
        return titlePanel;
    }
}

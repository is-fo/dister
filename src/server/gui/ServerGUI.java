package server.gui;

import server.ChatServer;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;

/**
 * TODO implement <a href="https://github.com/java-native-access/jna">this</a> for iconify and dragging
 */
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

    public static Font OCRA12;
    public static Font MATRIX16;

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setBounds(WIDTH - (BUTTON_DIM * 2), 0, ( 2 * BUTTON_DIM), BUTTON_DIM);

        buttonPanel.add(minimizeButton());
        buttonPanel.add(createCloseButton());

        enableDraggable(titlePanel);

        buttonPanel.setOpaque(false);
        titlePanel.add(buttonPanel);
        titlePanel.setVisible(true);
        return titlePanel;
    }


    private static int pX, pY;
    /**
     *<a href="https://stackoverflow.com/questions/26318474/moving-a-jframe-with-custom-title-bar">Code modified from stackoverflow post</a>
     */
    private void enableDraggable(JPanel titlePanel) {
        titlePanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                pX = e.getX();
                pY = e.getY();
                titlePanel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                titlePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        titlePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                SwingUtilities.invokeLater(() -> {

                    int newX = window.getLocation().x + e.getX() - pX;
                    int newY = window.getLocation().y + e.getY() - pY;

                    int w11TaskBarYLocation = Toolkit.getDefaultToolkit().getScreenSize().height - 78;
                    if (newY > w11TaskBarYLocation) {
                        newY = w11TaskBarYLocation;
                    }
                    window.setLocation(newX, newY);
                });
            }
        });
    }

    private JLabel minimizeButton() {
        JLabel minimizeLabel = new JLabel("_");
        minimizeLabel.setBounds(16, -4, 24, BUTTON_DIM);
        minimizeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        minimizeLabel.setVerticalTextPosition(SwingConstants.CENTER);

        minimizeLabel.setForeground(INACTIVE);
        minimizeLabel.setFont(ICON);

        minimizeLabel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                minimizeLabel.setFont(sICON);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                minimizeLabel.setFont(ICON);
                if (minimizeLabel.contains(e.getPoint())) {
                    window.setExtendedState(JFrame.ICONIFIED);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeLabel.setForeground(MINIMIZE);
                minimizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimizeLabel.setForeground(INACTIVE);
                minimizeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return minimizeLabel;
    }

    private JLabel createCloseButton() {
        JLabel closeLabel = new JLabel();
        closeLabel.setBounds(BUTTON_DIM + 8, 3, 24, BUTTON_DIM);
        closeLabel.setText("x");
        closeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        closeLabel.setVerticalTextPosition(SwingConstants.CENTER);

        closeLabel.setForeground(INACTIVE);
        closeLabel.setFont(ICON);
        closeLabel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                closeLabel.setFont(sICON);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                closeLabel.setFont(ICON);
                if (closeLabel.contains(e.getPoint())) {
                    serverGUI = null;
                    chatServer.close();
                    window.dispose();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                closeLabel.setForeground(CLOSE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                closeLabel.setForeground(INACTIVE);
            }
        });

        return closeLabel;
    }

}

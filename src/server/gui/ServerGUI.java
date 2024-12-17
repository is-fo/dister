package server.gui;

import server.ChatServer;

import javax.swing.*;
import javax.swing.text.BadLocationException;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


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

    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final int BUTTON_DIM = 32;

    private JFrame window;
    private JPanel mainPanel;
    private JTextPane textPane;
    private StyledDocument styledDoc;
    private Style logStyle, errorStyle, timeStyle;

    private static ServerGUI serverGUI;
    private ChatServer chatServer;

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

    public static ServerGUI getInstance() {
        if (serverGUI == null) {
            //TODO ta bort o göra en annan klass för initFonts() och initStyles()
            throw new NullPointerException("Parameterless constructor only allowed to be used when ServerGUI isn't null.");
        }
        return serverGUI;
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
        mainPanel.add(textPane);

        JTextArea backdrop = createBackDrop(12f);
        mainPanel.add(backdrop);
        JTextArea backdrop2 = createBackDrop(16f);
        mainPanel.add(backdrop2);
        JTextArea backdrop3 = createBackDrop(20f);
        mainPanel.add(backdrop3);

        new Timer(40, e -> {
            matrixText(backdrop);
            matrixText(backdrop2);
            matrixText(backdrop3);
        }).start();

        window.add(mainPanel);
        window.setVisible(true);
    }

    private void matrixText(JTextArea textArea) {
        Random random = new Random();
        StringBuilder line = new StringBuilder();
        int maxChars = mainPanel.getWidth() / (int) textArea.getFont().getSize2D() * 2;
        for (int i = 0; i < maxChars; i++) {
            if (random.nextInt(100) < 15) {
                line.append((char) (random.nextInt(26) + 0b11111));
            } else {
                line.append(" ");
            }
        }
        line.append("\n");

        try {
            textArea.getDocument().insertString(0, line.toString(), null);
        } catch (BadLocationException e) {
            System.err.println("Matrix error: BadLocationException: " + e);
        }

        int maxLines = mainPanel.getHeight() / (int) textArea.getFont().getSize2D(); //seems to work
        if (textArea.getLineCount() > maxLines) {
            try {
                textArea.replaceRange("", textArea.getLineEndOffset(maxLines - 1), textArea.getLineEndOffset(maxLines));
            } catch (BadLocationException e) {
                System.err.println("Matrix error: BadLocationException: " + e);
            }
        }
    }

    public JTextArea createBackDrop(Float fontSize) {
        JTextArea backdrop = new JTextArea();
        backdrop.setEditable(false);
        int modifier = (((int) (double)fontSize) - 10) * 8; //lol
        backdrop.setForeground(new Color(BACKDROP.getRed(), BACKDROP.getGreen() + modifier, BACKDROP.getBlue() + modifier));
        backdrop.setOpaque(false);                   //space for titlebar + bottom command line
        backdrop.setBounds(0, BUTTON_DIM, WIDTH, mainPanel.getHeight() - (BUTTON_DIM * 2));
        backdrop.setFont(MATRIX16.deriveFont(fontSize));

        return backdrop;
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

        limitLineCount(window.getHeight() / ((int)OCRA12.getSize2D() + 3));
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
    private static Robot robot;
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(110101);
        }
    }
    /**
     *<a href="https://stackoverflow.com/questions/26318474/moving-a-jframe-with-custom-title-bar">Code modified from stackoverflow post</a>
     */
    private void enableDraggable(JPanel titlePanel) {
        titlePanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                pX = e.getX();
                pY = e.getY();
            }
        });

        titlePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                SwingUtilities.invokeLater(() -> {

                    int newX = window.getLocation().x + e.getX() - pX;
                    int newY = window.getLocation().y + e.getY() - pY;

                    int w11TaskBarYLocation = Toolkit.getDefaultToolkit().getScreenSize().height - 78;
                    if (newY <= 0) {
                        robot.mouseMove(window.getLocation().x + pX, window.getLocation().y + pY);
                        newY = 0;
                    } else if (newY > w11TaskBarYLocation) {
                        robot.mouseMove(window.getLocation().x + pX, window.getLocation().y + pY);
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

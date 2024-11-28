package server.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.ObjectInputStream;

public class ServerGUI {

    public static final Color RED = new Color(200, 22, 22);
    public static final Color BLUE = new Color(22, 155, 233);
    public static final Color WHITE = new Color(225, 255, 255);
    public static final Font DEFAULT_FONT = new Font("Consolas", Font.BOLD, 32);
    public static final Font SMALLER_FONT = new Font("Consolas", Font.PLAIN, 28);

    private final int WIDTH = 600;
    private final int BUTTON_DIM = 32;

    private int pX, pY;
    Robot robot;
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(110101);
        }
    }

    private final String ICON_PATH = "src/server/gui/icons/";

    JFrame window = new JFrame("SERVERLOGS");
    JPanel mainPanel = new JPanel();

    private final ObjectInputStream in;

    public ServerGUI(ObjectInputStream in) {
        this.in = in;
    }

    private void createWindow() {
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLayout(null);
//        window.setBackground(new Color(33, 33, 33));
        window.setSize(WIDTH, 800);
        window.setLocationRelativeTo(null);
        window.setUndecorated(true);
        window.add(mainPanel);

        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, WIDTH, 800);
        mainPanel.setBackground(new Color(33, 33, 33));

        mainPanel.add(createCustomTitleBar());


        window.setVisible(true);
    }

    private JPanel createCustomTitleBar() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(null);
        titlePanel.setBounds(0, 0, WIDTH, BUTTON_DIM);
        titlePanel.setBackground(new Color(99, 99, 99));

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


    /**
     *https://stackoverflow.com/questions/26318474/moving-a-jframe-with-custom-title-bar
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
        minimizeLabel.setBounds(8, -5, 24, BUTTON_DIM);
        minimizeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        minimizeLabel.setVerticalTextPosition(SwingConstants.CENTER);

        minimizeLabel.setForeground(WHITE);
        minimizeLabel.setFont(DEFAULT_FONT);

        minimizeLabel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                minimizeLabel.setFont(SMALLER_FONT);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                minimizeLabel.setFont(DEFAULT_FONT);
                if (minimizeLabel.contains(e.getPoint())) {
                    window.setExtendedState(JFrame.ICONIFIED);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeLabel.setForeground(BLUE);
                minimizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimizeLabel.setForeground(WHITE);
                minimizeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return minimizeLabel;
    }


    private JLabel createCloseButton() {
        JLabel closeLabel = new JLabel();
        closeLabel.setBounds(BUTTON_DIM, 2, 24, BUTTON_DIM);
        closeLabel.setText("x");
        closeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        closeLabel.setVerticalTextPosition(SwingConstants.CENTER);

        closeLabel.setForeground(WHITE);
        closeLabel.setFont(DEFAULT_FONT);
        closeLabel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                closeLabel.setFont(SMALLER_FONT);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                closeLabel.setFont(DEFAULT_FONT);
                if (closeLabel.contains(e.getPoint())) {
                    window.dispose();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                closeLabel.setForeground(RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                closeLabel.setForeground(WHITE);
            }
        });

        return closeLabel;
    }

    public static void main(String[] args) {
        ServerGUI serverGUI = new ServerGUI(null);
        serverGUI.createWindow();
    }


}

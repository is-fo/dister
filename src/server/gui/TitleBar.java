package server.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import static server.gui.ServerGUI.*;

public class TitleBar extends JPanel {

    private JFrame window;
    private JLabel minimize;
    private JLabel close;

    public TitleBar(JFrame window) {
        this.window = window;
        init();
    }

    private void init() {
        minimizeButton();
        this.add(minimize);
        createCloseButton();
        this.add(close);
    }

    private void minimizeButton() {
        minimize = new JLabel("_");
        minimize.setBounds(16, -4, 24, BUTTON_DIM);
        minimize.setHorizontalTextPosition(SwingConstants.CENTER);
        minimize.setVerticalTextPosition(SwingConstants.CENTER);

        minimize.setForeground(INACTIVE);
        minimize.setFont(ICON);

        minimize.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                minimize.setFont(sICON);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                minimize.setFont(ICON);
                if (minimize.contains(e.getPoint())) {
                    window.setExtendedState(JFrame.ICONIFIED);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimize.setForeground(MINIMIZE);
                minimize.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimize.setForeground(INACTIVE);
                minimize.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void createCloseButton() {
        close = new JLabel();
        close.setBounds(BUTTON_DIM + 8, 3, 24, BUTTON_DIM);
        close.setText("x");
        close.setHorizontalTextPosition(SwingConstants.CENTER);
        close.setVerticalTextPosition(SwingConstants.CENTER);

        close.setForeground(INACTIVE);
        close.setFont(ICON);
        close.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                close.setFont(sICON);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                close.setFont(ICON);
                if (close.contains(e.getPoint())) {
                    window.dispose();
                    //TODO stäng av logik
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                close.setCursor(new Cursor(Cursor.HAND_CURSOR));
                close.setForeground(CLOSE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                close.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                close.setForeground(INACTIVE);
            }
        });
    }
}

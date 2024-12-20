package server.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Draggable {
    private JFrame window;
    private static int pX, pY;

    public Draggable(JFrame window) {
        this.window = window;
    }
    /**
     * TODO implement <a href="https://github.com/java-native-access/jna">this</a> for iconify and dragging

     *<a href="https://stackoverflow.com/questions/26318474/moving-a-jframe-with-custom-title-bar">Current code modified from stackoverflow post</a>
     */
    public void enableDraggable(JPanel titlePanel) {
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
}

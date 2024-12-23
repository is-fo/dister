package server.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.Random;

import static server.gui.ServerGUI.*;

public class MatrixBackdrop {

    private final JPanel target;

    public MatrixBackdrop(JPanel target) {
        this.target = target;
    }

    /**
     *
     * @param amount LAG PARAMETER =)
     * @param fontSizes 2D font size, populated by random sizes if empty/incomplete
     * @param delay timer delay in ms
     *
     */
    public void startMatrixText(int amount, float[] fontSizes, int delay) {
        if (amount > fontSizes.length) {
            int startIndex = fontSizes.length - 1; //fails if empty array but MECARENO
            fontSizes = new float[amount];
            for (int i = startIndex; i < amount; i++) {
                fontSizes[i] = new Random().nextInt(32) + 4;
            }
        }
        JTextArea[] texts = new JTextArea[amount];
        for (int i = 0; i < amount; i++) {
            texts[i] = createBackDrop(fontSizes[i]);
            target.add(texts[i]);
            final int copy = i;
            new Timer(delay, _ -> matrixText(texts[copy])).start();
        }
    }

    public void startMatrixText(int amount, int delay) {
        float[] fontSizes = new float[amount];
        for (int i = 0; i < amount; i++) {
            fontSizes[i] = new Random().nextInt(32) + 4;
        }
        startMatrixText(amount, fontSizes, delay);
    }

    /**
     * default matrix text configuration
     */
    public void startMatrixText() {
        startMatrixText(3, new float[]{14f, 16f, 20f}, 36);
    }

    private void matrixText(JTextArea textArea) {
        Random random = new Random();
        StringBuilder line = new StringBuilder();
        int textWidth = (int) textArea.getFont().getSize2D() / 2;
        int maxChars = target.getWidth() / textWidth;
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
            e.printStackTrace();
            System.err.println("Matrix error: BadLocationException: " + e);
        }

        int textHeight = (int) textArea.getFont().getSize2D();
        int maxLines = target.getHeight() / textHeight; //seems to work
        if (textArea.getLineCount() > maxLines) {
            try {
                textArea.replaceRange("", textArea.getLineEndOffset(maxLines - 1), textArea.getLineEndOffset(maxLines));
            } catch (BadLocationException e) {
                e.printStackTrace();
                System.err.println("Matrix error: BadLocationException: " + e);
            }
        }
    }

    private JTextArea createBackDrop(Float fontSize) {
        JTextArea backdrop = new JTextArea();
        backdrop.setEditable(false);
        backdrop.setOpaque(false);
        backdrop.setFont(MATRIX16.deriveFont(fontSize));

        //används för att ändra färgen, större färger får mer färg
        int modifier = (((int) (double)fontSize) - 10) * 8; //lol
        modifier = Math.min(modifier, 255);
        backdrop.setForeground(new Color(BACKDROP.getRed(), BACKDROP.getGreen() + modifier, BACKDROP.getBlue() + modifier));
        return backdrop;
    }
}

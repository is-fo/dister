package server.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.Random;

import static server.gui.ServerGUI.*;

public class MatrixBackdrop {
    /**
     *
     * @param target the panel to add the falling text effect to
     * @param amount LAG PARAMETER =)
     * @param fontSizes 2D font size, populated by random sizes if empty/incomplete
     * @param delay timer delay in ms
     *
     */
    public void startMatrixText(JPanel target, int amount, float[] fontSizes, int delay) {
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
            new Timer(delay, _ -> {
                matrixText(texts[copy]);
            }).start();
        }
    }

    public void startMatrixText(JPanel target, int amount, int delay) {
        float[] fontSizes = new float[amount];
        for (int i = 0; i < amount; i++) {
            fontSizes[i] = new Random().nextInt(32) + 4;
        }
        startMatrixText(target, amount, fontSizes, delay);
    }

    /**
     *
     * @param target adds a default matrix text effect to target panel
     */
    public void startMatrixText(JPanel target) {
        startMatrixText(target, 3, new float[]{14f, 16f, 20f}, 36);
    }

    private void matrixText(JTextArea textArea) {
        Random random = new Random();
        StringBuilder line = new StringBuilder();
        int textWidth = (int) textArea.getFont().getSize2D() / 2;
        int maxChars = WIDTH / textWidth;
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

        int textHeight = (int) textArea.getFont().getSize2D();
        int maxLines = HEIGHT / textHeight; //seems to work
        if (textArea.getLineCount() > maxLines) {
            try {
                textArea.replaceRange("", textArea.getLineEndOffset(maxLines - 1), textArea.getLineEndOffset(maxLines));
            } catch (BadLocationException e) {
                System.err.println("Matrix error: BadLocationException: " + e);
            }
        }
    }

    private JTextArea createBackDrop(Float fontSize) {
        JTextArea backdrop = new JTextArea();
        backdrop.setEditable(false);
        int modifier = (((int) (double)fontSize) - 10) * 8; //lol
        modifier = modifier > 255 ? 255 : modifier;
        System.out.println(modifier);
        backdrop.setForeground(new Color(BACKDROP.getRed(), BACKDROP.getGreen() + modifier, BACKDROP.getBlue() + modifier));
        backdrop.setOpaque(false);                   //space for titlebar + bottom command line
        backdrop.setBounds(0, BUTTON_DIM, WIDTH, HEIGHT - (BUTTON_DIM * 2));
        backdrop.setFont(MATRIX16.deriveFont(fontSize));

        return backdrop;
    }


}

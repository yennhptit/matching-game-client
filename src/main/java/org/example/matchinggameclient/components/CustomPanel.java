package org.example.matchinggameclient.components;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {
    private int cornerRadius;
    private float transparency;

    public CustomPanel(int cornerRadius, float transparency) {
        super();
        this.cornerRadius = cornerRadius;
        this.transparency = transparency;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color backgroundColor = new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), (int) (transparency * 255));
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
    }
}

package com.raytracer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class RenderWindow {
    private JFrame frame;
    private JPanel panel;
    private BufferedImage image;

    public boolean movingForward, movingBackward, movingLeft, movingRight;
    public boolean isMoving;

    public RenderWindow(int width, int height, BufferedImage framebuffer){
        this.image = framebuffer;

        frame = new JFrame("Multi-Threaded Ray Tracer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                updateMovement(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e){
                updateMovement(e.getKeyCode(), false);
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void updateMovement(int keyCode, boolean state) {
        if (keyCode == KeyEvent.VK_W) movingForward = state;
        if (keyCode == KeyEvent.VK_S) movingBackward = state;
        if (keyCode == KeyEvent.VK_A) movingLeft = state;
        if (keyCode == KeyEvent.VK_D) movingRight = state;
        
        isMoving = movingForward || movingBackward || movingLeft || movingRight;
    }
    
    public void refresh() {
        panel.repaint();
    }
}

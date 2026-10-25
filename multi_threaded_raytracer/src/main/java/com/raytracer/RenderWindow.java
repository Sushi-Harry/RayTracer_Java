package com.raytracer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class RenderWindow {
    private JFrame frame;
    private JPanel panel;
    private BufferedImage image;

    public volatile double targetX = 0.0, targetY = 0.0, targetZ = -1.0;
    public volatile boolean isGuiMoving = false;

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

        // This is for GUI
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(200, height));
        controlPanel.setBorder(BorderFactory.createTitledBorder("[Sphere Controls]"));

        controlPanel.add(new JLabel("X Position"));
        controlPanel.add(createCoordinateSlider(0));
        controlPanel.add(new JLabel("Y Position"));
        controlPanel.add(createCoordinateSlider(1));
        controlPanel.add(new JLabel("Z Position"));
        controlPanel.add(createCoordinateSlider(2));

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.EAST);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JSlider createCoordinateSlider(int axis){
        JSlider slider = new JSlider(0, 400, 200);

        slider.addChangeListener(e -> {
            double mappedValue = (slider.getValue() - 200) / 100.0;
            if(axis == 0) targetX = mappedValue;
            if(axis == 1) targetY = mappedValue;
            if(axis == 2) targetZ = mappedValue;

            isGuiMoving = true;
        });

        slider.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseReleased(MouseEvent e){
                isGuiMoving = false;
            }
        });

        return slider;
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

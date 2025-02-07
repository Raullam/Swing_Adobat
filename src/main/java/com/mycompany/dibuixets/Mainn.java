package com.mycompany.dibuixets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Mainn {
    public static void main(String[] args) {
        // Crear el frame principal
        JFrame mainFrame = new JFrame("Frame Principal");

        // Método para crear el menú principal
        JPanel mainMenu = createMainMenu(mainFrame);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(mainMenu, BorderLayout.CENTER);
        mainFrame.setSize(400, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static JPanel createMainMenu(JFrame mainFrame) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton button1 = new JButton("Detector de Cares");
        JButton button2 = new JButton("Croma");
        JButton button3 = new JButton("Botón 3");
        JButton button4 = new JButton("Paint 2030");

        button1.setPreferredSize(new Dimension(200, 40));
        button2.setPreferredSize(new Dimension(200, 40));
        button3.setPreferredSize(new Dimension(200, 40));
        button4.setPreferredSize(new Dimension(200, 40));

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel j = new RealTimeFaceDetection();
                j.setLayout(null);
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel j = new Croma();
                j.setLayout(null);
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel j = new TextRecognition();
                j.setLayout(null);
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDrawingPanel(mainFrame);
            }
        });

        buttonPanel.add(button1, gbc);
        gbc.gridy++;
        buttonPanel.add(button2, gbc);
        gbc.gridy++;
        buttonPanel.add(button3, gbc);
        gbc.gridy++;
        buttonPanel.add(button4, gbc);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private static void openDrawingPanel(JFrame mainFrame) {
        String imagePath = "images\\,,nk.jpg";
        OpenCVDrawingApp2 drawingPanel = new OpenCVDrawingApp2(imagePath);
        drawingPanel.setPreferredSize(new Dimension(1200, 400));

        int imageWidth = drawingPanel.getPreferredSize().width;
        int imageHeight = drawingPanel.getPreferredSize().height;
        mainFrame.setSize(imageWidth, imageHeight + 100);

        mainFrame.getContentPane().removeAll();
        mainFrame.add(drawingPanel, BorderLayout.WEST);
        JPanel controlPanel = OpenCVDrawingApp2.createControlPanel(drawingPanel);
        
        // Crear botón de volver al menú principal
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getContentPane().removeAll();
                mainFrame.add(createMainMenu(mainFrame), BorderLayout.CENTER);
                mainFrame.setSize(400, 400);
                mainFrame.revalidate();
                mainFrame.repaint();
                mainFrame.setLocationRelativeTo(null);
            }
        });
        
        controlPanel.add(backButton);
        mainFrame.add(controlPanel, BorderLayout.SOUTH);

        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setLocationRelativeTo(null);
    }
}

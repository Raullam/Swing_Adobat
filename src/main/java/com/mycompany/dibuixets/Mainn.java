package com.mycompany.dibuixets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase principal que crea el frame y el menú de la aplicación.
 * Esta clase sirve como el punto de entrada para la interfaz gráfica del usuario (GUI) y gestiona la lógica de navegación entre diferentes paneles.
 * 
 * @author [Tu nombre aquí]
 * @version 1.0
 * @since 2025-02-11
 */
public class Mainn {
    
    /**
     * Método principal que crea el frame principal y agrega el menú.
     * Este método establece la ventana principal, su tamaño, y lo hace visible.
     *
     * @param args Argumentos de línea de comandos (no utilizados en este caso).
     */
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

    /**
     * Crea el menú principal con botones para diferentes funcionalidades.
     * 
     * @param mainFrame El frame principal que contiene los botones.
     * @return Un JPanel que contiene los botones del menú principal.
     */
    private static JPanel createMainMenu(JFrame mainFrame) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Crear botones para diferentes funciones
        JButton button1 = new JButton("Detector de Cares");
        JButton button2 = new JButton("Croma");
        JButton button3 = new JButton("Botón 3");
        JButton button4 = new JButton("Paint 2030");

        // Establecer el tamaño preferido de los botones
        button1.setPreferredSize(new Dimension(200, 40));
        button2.setPreferredSize(new Dimension(200, 40));
        button3.setPreferredSize(new Dimension(200, 40));
        button4.setPreferredSize(new Dimension(200, 40));

        // Acción para el botón de detector de caras
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel j = new RealTimeFaceDetection();
                j.setLayout(null);
            }
        });
        
        // Acción para el botón de Croma
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel j = new Croma();
                j.setLayout(null);
            }
        });
        
        // Acción para el botón de reconocimiento de texto
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel j = new TextRecognition();
                j.setLayout(null);
            }
        });

        // Acción para el botón Paint 2030
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDrawingPanel(mainFrame);
            }
        });

        // Agregar los botones al panel
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

    /**
     * Abre el panel de dibujo y muestra una imagen con controles.
     * Establece el tamaño del frame según las dimensiones de la imagen.
     * Además, agrega un botón para volver al menú principal.
     * 
     * @param mainFrame El frame principal donde se visualizará el panel de dibujo.
     */
    private static void openDrawingPanel(JFrame mainFrame) {
        String imagePath = "images\\,,nk.jpg";  // Ruta de la imagen a mostrar
        OpenCVDrawingApp2 drawingPanel = new OpenCVDrawingApp2(imagePath);
        drawingPanel.setPreferredSize(new Dimension(1200, 400));

        int imageWidth = drawingPanel.getPreferredSize().width;
        int imageHeight = drawingPanel.getPreferredSize().height;
        mainFrame.setSize(imageWidth, imageHeight + 100);

        // Eliminar los componentes existentes y agregar el panel de dibujo
        mainFrame.getContentPane().removeAll();
        mainFrame.add(drawingPanel, BorderLayout.WEST);
        
        // Crear el panel de controles del dibujo
        JPanel controlPanel = OpenCVDrawingApp2.createControlPanel(drawingPanel);
        
        // Crear el botón de volver al menú principal
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Volver al menú principal
                mainFrame.getContentPane().removeAll();
                mainFrame.add(createMainMenu(mainFrame), BorderLayout.CENTER);
                mainFrame.setSize(400, 400);
                mainFrame.revalidate();
                mainFrame.repaint();
                mainFrame.setLocationRelativeTo(null);
            }
        });
        
        // Agregar el botón de volver al panel de control
        controlPanel.add(backButton);
        mainFrame.add(controlPanel, BorderLayout.SOUTH);

        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setLocationRelativeTo(null);
    }
}

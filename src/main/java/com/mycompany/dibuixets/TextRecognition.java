package com.mycompany.dibuixets;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class TextRecognition extends JPanel {
    private VideoCapture camera;
    private Mat frame;
    private BufferedImage bufferedImage;
    private String capturedImagePath = "images/captured_image.jpg";
    private JFrame frameWindow;

    public TextRecognition() {
        // Cargar OpenCV
        System.load("C:\\Users\\Rulox\\Downloads\\opencv\\build\\java\\x64\\opencv_java490.dll");

        // Inicializar la cámara
        camera = new VideoCapture(0, Videoio.CAP_DSHOW);
        frame = new Mat();

        // Crear botón para guardar imagen
        JButton saveButton = new JButton("Guardar Imagen");
        saveButton.addActionListener(e -> saveCapturedImage());

        // Crear botón para volver (cerrar la ventana)
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra solo la ventana de la cámara (no la aplicación completa)
                camera.release();
                frameWindow.dispose();
            }
        });

        // Panel con los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));  // Layout para los botones en línea horizontal
        buttonPanel.add(Box.createHorizontalGlue()); // Esto coloca los botones al centro
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // Espacio entre los botones
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalGlue());

        // Configurar ventana
        frameWindow = new JFrame("Real-Time Text Detection");
        frameWindow.setLayout(new BorderLayout());
        frameWindow.add(this, BorderLayout.CENTER); // Panel con la detección de texto en el centro
        frameWindow.add(buttonPanel, BorderLayout.SOUTH); // Los botones al sur
        frameWindow.setSize(640, 480);
        frameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Centrar la ventana en la pantalla
        frameWindow.setLocationRelativeTo(null);  // Esto centra la ventana en la pantalla
        frameWindow.setVisible(true);

        // Iniciar procesamiento de fotogramas
        new Thread(() -> {
            while (true) {
                if (camera.read(frame)) {
                    Mat grayFrame = new Mat();
                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.threshold(grayFrame, grayFrame, 100, 255, Imgproc.THRESH_BINARY);

                    // Solo ejecutar OCR cada 6 segundos
                    long startTime = System.currentTimeMillis();
                    if (startTime % 6000 < 100) { // Comprobar si han pasado 6 segundos
                        String detectedText = detectText(grayFrame);
                        if (detectedText != null && !detectedText.isEmpty()) {
                            System.out.println("Texto Detectado: " + detectedText);
                        }
                    }

                    // Convertir a BufferedImage y redibujar
                    bufferedImage = matToBufferedImage(frame);
                    repaint();

                    try {
                        Thread.sleep(100); // Pequeña pausa para reducir carga
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            g.drawImage(bufferedImage, 0, 0, this);
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();
        BufferedImage image = (channels == 1) 
                ? new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
                : new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        byte[] data = new byte[width * height * channels];
        mat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, width, height, data);

        return image;
    }

    private String detectText(Mat frame) {
        ITesseract instance = new Tesseract();
        instance.setDatapath("C:\\Users\\Rulox\\Downloads\\Nueva carpeta (83)\\dibuxets222\\dibuixets\\src\\tessdata");
        instance.setLanguage("eng");

        BufferedImage image = matToBufferedImage(frame);
        try {
            return instance.doOCR(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveCapturedImage() {
        if (frame != null) {
            String fileName = JOptionPane.showInputDialog("Introduce el nombre para guardar la imagen:");
            if (fileName != null && !fileName.trim().isEmpty()) {
                File outputFile = new File("images/" + fileName + ".jpg");
                Imgcodecs.imwrite(outputFile.getAbsolutePath(), frame);
                JOptionPane.showMessageDialog(this, "Imagen guardada como: " + outputFile.getAbsolutePath());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha detectado ninguna imagen.");
        }
    }

    public static void main(String[] args) {
        new TextRecognition();
    }
}

package com.mycompany.dibuixets;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import org.opencv.imgproc.Imgproc;

public class Croma extends JPanel {
    private Mat frame;
    private BufferedImage bufferedImage;
    private VideoCapture capture;
    private Scalar lowerGreen = new Scalar(35, 50, 50);  // Llindar inferior del color verd
    private Scalar upperGreen = new Scalar(85, 255, 255); // Llindar superior del color verd
    private boolean cromaActive = false; // Estat de l'efecte croma
    private boolean capturing = true; // Control para detener la captura
    private Thread captureThread; // Hilo de captura de video

    /**
     * Constructor de la classe Croma. Inicialitza la captura de vídeo i la interfície gràfica.
     */
    public Croma() {
        // Cargar la librería de OpenCV
        System.load("C:\\Users\\Rulox\\Downloads\\Nueva carpeta (83)\\dibuxets222\\dibuixets\\src\\main\\java\\com\\mycompany\\dibuixets\\dll\\opencv_java490.dll");

        // Si ya existe un objeto de VideoCapture, cerrarlo antes de crear uno nuevo
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
        
        // Crear la captura de video
        capture = new VideoCapture(0); // Inicia la cámara
        frame = new Mat();

        if (!capture.isOpened()) {
            JOptionPane.showMessageDialog(this, "No s'ha pogut accedir a la càmera.");
            return;
        } else {
            System.out.println("Cámara abierta correctamente.");
        }

        // Crear botones
        JButton cromaButton = new JButton("Activar Croma");
        cromaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cromaActive = !cromaActive;
                cromaButton.setText(cromaActive ? "Desactivar Croma" : "Activar Croma");
            }
        });

        JButton volverButton = new JButton("Volver");
        volverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Detener la captura y liberar recursos al cerrar
                capturing = false;
                if (capture != null && capture.isOpened()) {
                    capture.release(); // Cerrar la cámara
                }
                Window window = SwingUtilities.getWindowAncestor(Croma.this);
                if (window != null) {
                    window.dispose(); // Cerrar la ventana
                }
            }
        });

        // Configurar el layout para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(cromaButton);
        buttonPanel.add(volverButton);

        this.setLayout(new BorderLayout());
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Hilo de captura en segundo plano
        captureThread = new Thread(() -> {
            while (capturing) {
                capture.read(frame);
                if (!frame.empty()) {
                    if (cromaActive) {
                        applyChromaKeyEffect(frame);
                    }
                    bufferedImage = matToBufferedImage(frame);
                    repaint();  // Solicitar la actualización de la imagen en el panel
                }
            }
        });
        captureThread.start();
    }

    /**
     * Aplica l'efecte de croma eliminant el fons verd.
     * 
     * @param frame Mat on s'aplicarà l'efecte.
     */
    private void applyChromaKeyEffect(Mat frame) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_BGR2HSV);

        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerGreen, upperGreen, mask);

        Mat background = new Mat(frame.size(), frame.type(), new Scalar(0, 255, 0));

        Mat result = new Mat();
        frame.copyTo(result, mask);
        background.copyTo(frame, mask);

        hsvImage.release();
        mask.release();
        background.release();
    }

    /**
     * Sobreescriu el mètode paintComponent per mostrar el vídeo processat.
     * 
     * @param g Objecte Graphics per pintar el component.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            g.drawImage(bufferedImage, 0, 0, this);  // Dibuja la imagen de la cámara
        }
    }

    /**
     * Converteix un Mat d'OpenCV a un BufferedImage per mostrar-lo a Swing.
     * 
     * @param mat Mat d'OpenCV.
     * @return BufferedImage equivalent.
     */
    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = new byte[width * height * (int) mat.elemSize()];
        mat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, width, height, data);
        return image;
    }

    /**
     * Mètode principal que inicia l'aplicació.
     * 
     * @param args Arguments de la línia de comandes.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Croma panel = new Croma();
            JFrame frame = new JFrame("Aplicació d'Efecte Croma amb OpenCV");
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.setLayout(new BorderLayout()); // Layout adecuado
            frame.setResizable(false);
            frame.setLocationRelativeTo(null); // Centrar la ventana
        });
    }
}

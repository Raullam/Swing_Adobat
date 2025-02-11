package com.mycompany.dibuixets;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import org.opencv.imgproc.Imgproc;

/**
 * Clase que implementa un panel de aplicación para aplicar el efecto de croma
 * (eliminación del fondo verde) en un flujo de video en tiempo real usando OpenCV.
 * La clase extiende JPanel y se encarga de capturar el video de la cámara, 
 * procesarlo para aplicar el efecto croma y mostrarlo en la interfaz gráfica de usuario (GUI).
 * 
 * <p> La interfaz gráfica incluye un botón para activar o desactivar el efecto de croma 
 * y un botón para cerrar la ventana y liberar los recursos del sistema. </p>
 * 
 * @author Usuario
 */
public class Croma extends JPanel {
    private Mat frame;  // Matriz para almacenar el frame actual de la cámara
    private BufferedImage bufferedImage;  // Imagen bufferizada para mostrarla en el panel
    private VideoCapture capture;  // Objeto para capturar el video desde la cámara
    private Scalar lowerGreen = new Scalar(35, 50, 50);  // Límite inferior del color verde para el croma
    private Scalar upperGreen = new Scalar(85, 255, 255); // Límite superior del color verde para el croma
    private boolean cromaActive = false;  // Estado de activación del efecto croma
    private boolean capturing = true;  // Control para detener la captura de video
    private Thread captureThread;  // Hilo que maneja la captura de video en segundo plano

    /**
     * Constructor de la clase Croma. Inicializa la captura de video y configura
     * la interfaz gráfica del panel con los botones correspondientes.
     * 
     * <p> Este constructor también configura el hilo de captura que se ejecuta en segundo plano, 
     * procesando el video de la cámara y aplicando el efecto de croma si está activado. </p>
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
                        applyChromaKeyEffect(frame);  // Aplicar el efecto croma si está activado
                    }
                    bufferedImage = matToBufferedImage(frame);  // Convertir la imagen de OpenCV a BufferedImage
                    repaint();  // Solicitar la actualización de la imagen en el panel
                }
            }
        });
        captureThread.start();
    }

    /**
     * Aplica el efecto de croma (eliminación del fondo verde) a la imagen capturada.
     * Este método utiliza un umbral de color verde en el espacio HSV para crear una máscara
     * y luego reemplaza el fondo verde con un fondo vacío.
     *
     * @param frame Mat objeto que contiene la imagen capturada desde la cámara.
     */
    private void applyChromaKeyEffect(Mat frame) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_BGR2HSV);  // Convertir la imagen a espacio de color HSV

        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerGreen, upperGreen, mask);  // Crear una máscara para el color verde

        Mat background = new Mat(frame.size(), frame.type(), new Scalar(0, 255, 0));  // Fondo verde

        Mat result = new Mat();
        frame.copyTo(result, mask);  // Copiar la parte de la imagen que no es verde
        background.copyTo(frame, mask);  // Reemplazar el fondo verde con el fondo vacío

        // Liberar recursos
        hsvImage.release();
        mask.release();
        background.release();
    }

    /**
     * Sobreescribe el método {@code paintComponent} para dibujar la imagen procesada
     * del video en el componente del panel.
     * 
     * @param g El objeto Graphics que se utiliza para dibujar en el panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            g.drawImage(bufferedImage, 0, 0, this);  // Dibuja la imagen de la cámara
        }
    }

    /**
     * Convierte un objeto {@code Mat} de OpenCV en un objeto {@code BufferedImage}
     * para poder mostrarlo en un componente Swing.
     *
     * @param mat El objeto Mat que contiene la imagen de OpenCV a convertir.
     * @return Un objeto BufferedImage equivalente a la imagen contenida en el Mat.
     */
    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = new byte[width * height * (int) mat.elemSize()];
        mat.get(0, 0, data);  // Obtener los datos de la imagen
        image.getRaster().setDataElements(0, 0, width, height, data);  // Establecer los datos en el BufferedImage
        return image;
    }

    /**
     * Método principal que inicia la aplicación y muestra el panel en una ventana JFrame.
     * 
     * @param args Argumentos de la línea de comandos (no utilizados en este caso).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Croma panel = new Croma();  // Crear el panel Croma
            JFrame frame = new JFrame("Aplicación de Efecto Croma con OpenCV");  // Crear la ventana principal
            frame.add(panel);
            frame.setSize(800, 600);  // Establecer el tamaño de la ventana
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);  // Hacer la ventana visible
            frame.setLayout(new BorderLayout());  // Layout adecuado para la ventana
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);  // Centrar la ventana
        });
    }
}

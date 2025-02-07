    package com.mycompany.dibuixets;

    import org.opencv.imgcodecs.Imgcodecs;
    import org.opencv.imgproc.Imgproc;

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.*;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import javax.swing.filechooser.FileNameExtensionFilter;
    import java.util.Stack;
    import org.opencv.core.Mat;
    import org.opencv.core.Scalar;

    public class OpenCVDrawingApp2 extends JPanel {
        private Mat image;
        private BufferedImage bufferedImage;
        private Point lastPoint;
        private Color currentColor = Color.RED;
        private int strokeWidth = 2;
        private boolean isErasing = false;
        private boolean isFreeDrawing = false;
        private String currentShape = "LINE";

        private Stack<Mat> undoStack = new Stack<>();
        private Stack<Mat> redoStack = new Stack<>();

        private JPanel myPanel;  // Panel que queremos hacer visible u oculto

        public OpenCVDrawingApp2(String imagePath) {
            // Inicialización de la imagen y demás
            System.load("C:\\Users\\Rulox\\Downloads\\Nueva carpeta (83)\\dibuxets222\\dibuixets\\src\\main\\java\\com\\mycompany\\dibuixets\\dll\\opencv_java490.dll");
            image = Imgcodecs.imread(imagePath);
            resizeImage();
            bufferedImage = matToBufferedImage(image);

            setPreferredSize(new Dimension(image.width(), image.height()));
            undoStack.push(image.clone());

            // Crear el panel adicional que estará oculto por defecto
            myPanel = new JPanel();
            myPanel.setBackground(Color.CYAN);
            myPanel.setPreferredSize(new Dimension(200, 200));
            myPanel.setVisible(false); // Establecer el panel como no visible por defecto

            // Agregar un MouseListener y MouseMotionListener, como lo tenías
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastPoint = e.getPoint();
                    undoStack.push(image.clone());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!isFreeDrawing && !isErasing && lastPoint != null) {
                        drawShape(e.getPoint(), true);
                        bufferedImage = matToBufferedImage(image);
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastPoint != null) {
                        if (isFreeDrawing) {
                            drawFreeDraw(e.getPoint());
                        } else if (isErasing) {
                            erase(e.getPoint());
                        } else {
                            drawShape(e.getPoint(), false);
                        }
                        bufferedImage = matToBufferedImage(image);
                        repaint();
                    }
                }
            });
        }

        // Método para redimensionar la imagen
        private void resizeImage() {
            int maxWidth = 800;
            int maxHeight = 800;
            int width = image.width();
            int height = image.height();

            if (width > maxWidth || height > maxHeight) {
                double aspectRatio = (double) width / height;
                int newWidth = maxWidth;
                int newHeight = (int) (newWidth / aspectRatio);
                if (newHeight > maxHeight) {
                    newHeight = maxHeight;
                    newWidth = (int) (newHeight * aspectRatio);
                }

                Mat resizedImage = new Mat();
                Imgproc.resize(image, resizedImage, new org.opencv.core.Size(newWidth, newHeight));
                image = resizedImage;
                bufferedImage = matToBufferedImage(image);
                setPreferredSize(new Dimension(newWidth, newHeight));
            }
        }

        // Método para dibujar figuras
        private void drawShape(Point currentPoint, boolean finalize) {
            image = undoStack.peek().clone();
            Scalar color = new Scalar(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());

            switch (currentShape) {
                case "CIRCLE":
                    int radius = (int) lastPoint.distance(currentPoint);
                    Imgproc.circle(image, new org.opencv.core.Point(lastPoint.x, lastPoint.y), radius, color, strokeWidth);
                    break;
                case "RECTANGLE":
                    Imgproc.rectangle(image, new org.opencv.core.Point(lastPoint.x, lastPoint.y),
                            new org.opencv.core.Point(currentPoint.x, currentPoint.y), color, strokeWidth);
                    break;
                case "ARROW":
                    Imgproc.arrowedLine(image, new org.opencv.core.Point(lastPoint.x, lastPoint.y),
                            new org.opencv.core.Point(currentPoint.x, currentPoint.y), color, strokeWidth);
                    break;
                case "LINE":
                    Imgproc.line(image, new org.opencv.core.Point(lastPoint.x, lastPoint.y),
                            new org.opencv.core.Point(currentPoint.x, currentPoint.y), color, strokeWidth);
                    break;
            }
            if (finalize) {
                undoStack.push(image.clone());
            }
        }

        // Método para el dibujo libre
        private void drawFreeDraw(Point currentPoint) {
            Scalar color = new Scalar(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
            Imgproc.line(image, new org.opencv.core.Point(lastPoint.x, lastPoint.y),
                    new org.opencv.core.Point(currentPoint.x, currentPoint.y), color, strokeWidth);
            lastPoint = currentPoint;
        }

        // Método para borrar
        private void erase(Point currentPoint) {
            Scalar eraserColor = new Scalar(255, 255, 255);
            Imgproc.circle(image, new org.opencv.core.Point(currentPoint.x, currentPoint.y), strokeWidth * 2, eraserColor, -1);
            lastPoint = currentPoint;
        }

        // Método para pintar el componente
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bufferedImage, 0, 0, this);
        }

        private BufferedImage matToBufferedImage(Mat mat) {
            int width = mat.width();
            int height = mat.height();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            byte[] data = new byte[width * height * (int) mat.elemSize()];
            mat.get(0, 0, data);
            image.getRaster().setDataElements(0, 0, width, height, data);
            return image;
        }

        // Método para crear el panel de controles
        public static JPanel createControlPanel(OpenCVDrawingApp2 panel) {
            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new FlowLayout());

            // Agregar botones de control
            JButton lineButton = new JButton("Línea");
            lineButton.addActionListener(e -> setDrawingMode(panel, "LINE", false, false));
            controlPanel.add(lineButton);

            JButton circleButton = new JButton("Círculo");
            circleButton.addActionListener(e -> setDrawingMode(panel, "CIRCLE", false, false));
            controlPanel.add(circleButton);

            JButton rectangleButton = new JButton("Rectángulo");
            rectangleButton.addActionListener(e -> setDrawingMode(panel, "RECTANGLE", false, false));
            controlPanel.add(rectangleButton);

            JButton arrowButton = new JButton("Flecha");
            arrowButton.addActionListener(e -> setDrawingMode(panel, "ARROW", false, false));
            controlPanel.add(arrowButton);

            JButton freeDrawButton = new JButton("Dibujo libre");
            freeDrawButton.addActionListener(e -> setDrawingMode(panel, "", true, false));
            controlPanel.add(freeDrawButton);

            JButton eraseButton = new JButton("Borrar");
            eraseButton.addActionListener(e -> setDrawingMode(panel, "", false, true));
            controlPanel.add(eraseButton);

            JButton undoButton = new JButton("Deshacer");
            undoButton.addActionListener(e -> panel.undo());
            controlPanel.add(undoButton);

            JButton redoButton = new JButton("Rehacer");
            redoButton.addActionListener(e -> panel.redo());
            controlPanel.add(redoButton);

            JButton colorButton = new JButton("Color");
            colorButton.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(panel, "Seleccionar Color", panel.currentColor);
                if (newColor != null) {
                    panel.currentColor = newColor;
                }
            });
            controlPanel.add(colorButton);

            JSlider thicknessSlider = new JSlider(1, 20, panel.strokeWidth);
            thicknessSlider.addChangeListener(e -> panel.strokeWidth = thicknessSlider.getValue());
            controlPanel.add(thicknessSlider);

            JButton saveButton = new JButton("Guardar");
            saveButton.addActionListener(e -> panel.saveImage());
            controlPanel.add(saveButton);
            
            JButton volver = new JButton("Volver");
            saveButton.addActionListener(e -> panel.saveImage());
            controlPanel.add(saveButton);


            return controlPanel;


        }

        // Método para configurar el modo de dibujo
        private static void setDrawingMode(OpenCVDrawingApp2 panel, String shape, boolean freeDraw, boolean erase) {
            panel.currentShape = shape;
            panel.isFreeDrawing = freeDraw;
            panel.isErasing = erase;
        }

        private void undo() {
            if (!undoStack.isEmpty()) {
                redoStack.push(image.clone());
                image = undoStack.pop();
                bufferedImage = matToBufferedImage(image);
                repaint();
            }
        }

        private void redo() {
            if (!redoStack.isEmpty()) {
                undoStack.push(image.clone());
                image = redoStack.pop();
                bufferedImage = matToBufferedImage(image);
                repaint();
            }
        }

        private void saveImage() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar imagen");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos JPEG", "jpg", "jpeg"));
            int userChoice = fileChooser.showSaveDialog(this);

            if (userChoice == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".jpg") && !file.getName().endsWith(".jpeg")) {
                    file = new File(file.getAbsolutePath() + ".jpg");
                }

                Imgcodecs.imwrite(file.getAbsolutePath(), image);
                JOptionPane.showMessageDialog(this, "Imagen guardada en: " + file.getAbsolutePath());
            }
        }
    }

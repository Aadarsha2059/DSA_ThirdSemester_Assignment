// question number 6 solutions...

package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileConverterrGUI extends JFrame {
    private JTextField fileTextField;
    private JComboBox<String> conversionTypeComboBox;
    private JProgressBar progressBar;
    private JTextArea statusTextArea;
    private JButton startButton, cancelButton;
    private JFileChooser fileChooser;
    private ExecutorService executorService;
    private SwingWorker<Void, ConversionTask> currentWorker;
    private GraphPanel graphPanel; // New component for displaying graph

    public FileConverterrGUI() {
        setTitle("File Converter & Route Optimization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        fileTextField = new JTextField();
        fileTextField.setEditable(false);
        JButton selectFileButton = new JButton("Select Files");
        conversionTypeComboBox = new JComboBox<>(new String[]{"PDF to DOCX", "Image Resize"});
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        startButton = new JButton("Start");
        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Files", "pdf", "jpg", "png"));

        // Graph panel for displaying delivery points and optimized routes
        graphPanel = new GraphPanel();
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Top panel for file selection
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(fileTextField, BorderLayout.CENTER);
        topPanel.add(selectFileButton, BorderLayout.EAST);

        // Middle panel for conversion options and progress
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        middlePanel.add(conversionTypeComboBox, BorderLayout.NORTH);
        middlePanel.add(progressBar, BorderLayout.CENTER);

        // Bottom panel for status and control buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(new JScrollPane(statusTextArea), BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(cancelButton);
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);

        // Right panel for graph display
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(new JScrollPane(graphPanel), BorderLayout.CENTER);

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // Event listeners
        selectFileButton.addActionListener(e -> selectFiles());
        startButton.addActionListener(e -> startConversion());
        cancelButton.addActionListener(e -> cancelConversion());

        // Executor service for managing threads
        executorService = Executors.newFixedThreadPool(4);
    }

    private void selectFiles() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            StringBuilder fileNames = new StringBuilder();
            for (File file : selectedFiles) {
                fileNames.append(file.getAbsolutePath()).append("; ");
            }
            fileTextField.setText(fileNames.toString());

            // Simulate adding delivery points to the graph
            graphPanel.setDeliveryPoints(new String[]{"Pokhara", "Kathmandu", "Lalbandi", "Biratnagar", "Nepalgunj"});
        }
    }

    private void startConversion() {
        File[] selectedFiles = fileChooser.getSelectedFiles();
        if (selectedFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No files selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String conversionType = (String) conversionTypeComboBox.getSelectedItem();
        progressBar.setValue(0);
        statusTextArea.setText("");
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);

        currentWorker = new SwingWorker<Void, ConversionTask>() {
            @Override
            protected Void doInBackground() {
                int totalFiles = selectedFiles.length;
                for (int i = 0; i < totalFiles && !isCancelled(); i++) {
                    File file = selectedFiles[i];
                    ConversionTask task = new ConversionTask(file, conversionType);
                    executorService.submit(task);
                    publish(task);

                    try {
                        task.get();
                    } catch (Exception e) {
                        publish(new ConversionTask(file, conversionType, e));
                    }

                    int progress = (int) ((i + 1) / (float) totalFiles * 100);
                    setProgress(progress);
                }
                return null;
            }

            @Override
            protected void process(List<ConversionTask> chunks) {
                for (ConversionTask task : chunks) {
                    if (task.getError() == null) {
                        statusTextArea.append("Converted: " + task.getFile().getName() + " (" + task.getType() + ")\n");
                    } else {
                        statusTextArea.append("Failed: " + task.getFile().getName() + " (" + task.getType() + ") - " + task.getError().getMessage() + "\n");
                    }
                }
            }

            @Override
            protected void done() {
                startButton.setEnabled(true);
                cancelButton.setEnabled(false);
                if (!isCancelled()) {
                    JOptionPane.showMessageDialog(FileConverterrGUI.this, "Conversion completed!", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(FileConverterrGUI.this, "Conversion cancelled!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };

        currentWorker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });

        currentWorker.execute();
    }

    private void cancelConversion() {
        if (currentWorker != null) {
            currentWorker.cancel(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileConverterrGUI converterGUI = new FileConverterrGUI();
            converterGUI.setVisible(true);
        });
    }

    // Class to handle individual file conversion
    private static class ConversionTask implements Runnable {
        private final File file;
        private final String type;
        private Exception error;

        public ConversionTask(File file, String type) {
            this.file = file;
            this.type = type;
        }

        public ConversionTask(File file, String type, Exception error) {
            this.file = file;
            this.type = type;
            this.error = error;
        }

        public File getFile() {
            return file;
        }

        public String getType() {
            return type;
        }

        public Exception getError() {
            return error;
        }

        @Override
        public void run() {
            try {
                // Simulate file conversion
                Thread.sleep(2000); // Simulating delay
                // Add actual file conversion logic here
            } catch (InterruptedException e) {
                error = e;
            }
        }

        public void get() {
        }
    }

    // Panel for displaying graph (delivery points and routes)
    private static class GraphPanel extends JPanel {
        private String[] deliveryPoints;

        public GraphPanel() {
            setPreferredSize(new Dimension(300, 300));
        }

        public void setDeliveryPoints(String[] deliveryPoints) {
            this.deliveryPoints = deliveryPoints;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (deliveryPoints != null) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = 100;
                int numPoints = deliveryPoints.length;
                double angleIncrement = 2 * Math.PI / numPoints;

                // Draw delivery points as nodes
                for (int i = 0; i < numPoints; i++) {
                    double angle = i * angleIncrement;
                    int x = centerX + (int) (radius * Math.cos(angle));
                    int y = centerY + (int) (radius * Math.sin(angle));
                    g.setColor(Color.BLUE);
                    g.fillOval(x - 10, y - 10, 20, 20);
                    g.setColor(Color.BLACK);
                    g.drawString(deliveryPoints[i], x - 10, y - 15);
                }

                // Draw routes (for demonstration purposes, routes are not optimized in this example)
                g.setColor(Color.RED);
                for (int i = 0; i < numPoints - 1; i++) {
                    double angle1 = i * angleIncrement;
                    double angle2 = (i + 1) * angleIncrement;
                    int x1 = centerX + (int) (radius * Math.cos(angle1));
                    int y1 = centerY + (int) (radius * Math.sin(angle1));
                    int x2 = centerX + (int) (radius * Math.cos(angle2));
                    int y2 = centerY + (int) (radius * Math.sin(angle2));
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }
}

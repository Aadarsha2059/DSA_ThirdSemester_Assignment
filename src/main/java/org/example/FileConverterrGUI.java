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
    private JPanel resultPanel;
    private DefaultListModel<File> fileListModel;

    public FileConverterrGUI() {
        setTitle("File Converter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        fileListModel = new DefaultListModel<>();
        JList<File> fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);
        topPanel.add(selectFileButton, BorderLayout.SOUTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        middlePanel.add(conversionTypeComboBox, BorderLayout.NORTH);
        middlePanel.add(progressBar, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(new JScrollPane(statusTextArea), BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(cancelButton);
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(new JScrollPane(resultPanel), BorderLayout.EAST);

        selectFileButton.addActionListener(e -> selectFiles());
        startButton.addActionListener(e -> startConversion());
        cancelButton.addActionListener(e -> cancelConversion());

        executorService = Executors.newFixedThreadPool(4);
    }

    private void selectFiles() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            fileListModel.clear();
            for (File file : selectedFiles) {
                fileListModel.addElement(file);
            }
        }
    }

    private void startConversion() {
        if (fileListModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "No files selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File[] selectedFiles = new File[fileListModel.getSize()];
        for (int i = 0; i < fileListModel.getSize(); i++) {
            selectedFiles[i] = fileListModel.get(i);
        }

        String conversionType = (String) conversionTypeComboBox.getSelectedItem();
        progressBar.setValue(0);
        statusTextArea.setText("");
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);

        resultPanel.removeAll();

        currentWorker = new SwingWorker<>() {
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
                        addResult(task.getFile());
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

    private void addResult(File file) {
        JLabel resultLabel = new JLabel("Converted File: " + file.getName());
        resultPanel.add(resultLabel);
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileConverterrGUI converterGUI = new FileConverterrGUI();
            converterGUI.setVisible(true);
        });
    }

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
                Thread.sleep(2000);
                // Implement actual conversion logic here
            } catch (InterruptedException e) {
                error = e;
            }
        }

        public void get() {
        }
    }
}

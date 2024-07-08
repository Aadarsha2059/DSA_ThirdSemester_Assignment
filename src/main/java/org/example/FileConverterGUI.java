
//Question Number 6 solutions.....

package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class FileConverterGUI extends JFrame {
    private JProgressBar progressBar;
    private JTextArea statusArea;
    private JButton startButton;
    private JButton cancelButton;
    private JComboBox<String> conversionOptions;
    private JFileChooser fileChooser;
    private java.util.List<File> selectedFiles;
    private SwingWorker<Void, ConversionTask> worker;

    public FileConverterGUI() {
        setTitle("File Converter");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // File chooser
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("All Files", "*.*"));

        // Conversion options
        conversionOptions = new JComboBox<>(new String[]{"PDF to Docx", "Image Resize"});
        JPanel optionsPanel = new JPanel();
        optionsPanel.add(new JLabel("Select Conversion Type:"));
        optionsPanel.add(conversionOptions);

        // Buttons
        startButton = new JButton("Start");
        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        // Status area
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);

        // Layout
        add(optionsPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(progressBar, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);

        // Button actions
        startButton.addActionListener(new StartButtonListener());
        cancelButton.addActionListener(new CancelButtonListener());
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFiles = List.of(fileChooser.getSelectedFiles());
                startButton.setEnabled(false);
                cancelButton.setEnabled(true);
                statusArea.setText("");

                worker = new ConversionWorker(selectedFiles, (String) conversionOptions.getSelectedItem());
                worker.execute();
            }
        }
    }

    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (worker != null && !worker.isDone()) {
                worker.cancel(true);
            }
        }
    }

    private class ConversionWorker extends SwingWorker<Void, ConversionTask> {
        private final List<File> files;
        private final String conversionType;

        public ConversionWorker(List<File> files, String conversionType) {
            this.files = files;
            this.conversionType = conversionType;
        }

        @Override
        protected Void doInBackground() {
            int totalFiles = files.size();
            for (int i = 0; i < totalFiles; i++) {
                if (isCancelled()) {
                    break;
                }
                File file = files.get(i);
                ConversionTask task = new ConversionTask(file, conversionType);
                try {
                    task.run();
                } catch (Exception e) {
                    task.setStatus("Failed: " + e.getMessage());
                }
                publish(task);
                int progress = (int) (((i + 1) / (double) totalFiles) * 100);
                setProgress(progress);
            }
            return null;
        }

        @Override
        protected void process(List<ConversionTask> chunks) {
            for (ConversionTask task : chunks) {
                statusArea.append(task.toString() + "\n");
            }
        }

        @Override
        protected void done() {
            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
            try {
                get();
                JOptionPane.showMessageDialog(null, "Conversion Completed!");
            } catch (InterruptedException | ExecutionException e) {
                JOptionPane.showMessageDialog(null, "Conversion Interrupted!");
            } catch (CancellationException e) {
                JOptionPane.showMessageDialog(null, "Conversion Cancelled!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileConverterGUI gui = new FileConverterGUI();
            gui.setVisible(true);
        });
    }
}

class ConversionTask {
    private final File file;
    private final String conversionType;
    private String status;

    public ConversionTask(File file, String conversionType) {
        this.file = file;
        this.conversionType = conversionType;
        this.status = "Pending";
    }

    public void run() throws Exception {
        // Simulate file conversion
        Thread.sleep(1000);  // Simulate time-consuming task
        this.status = "Completed";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return file.getName() + " - " + conversionType + ": " + status;
    }
}

// So executing the above codes addressing the given conditions in the question I developed a File Converter UI
// as the output



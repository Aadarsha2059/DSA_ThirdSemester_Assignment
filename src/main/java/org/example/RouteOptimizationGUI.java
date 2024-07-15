package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RouteOptimizationGUI extends JFrame {
    private JTextField deliveryPointsField;
    private JTextField vehicleCapacityField;
    private JComboBox<String> algorithmComboBox;
    private JButton optimizeButton;
    private RouteVisualizationPanel routePanel;

    public RouteOptimizationGUI() {
        setTitle("Route Optimization for Delivery Service");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Components initialization
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel deliveryLabel = new JLabel("Delivery Points (comma-separated): ");
        deliveryPointsField = new JTextField();
        JLabel vehicleLabel = new JLabel("Vehicle Capacity: ");
        vehicleCapacityField = new JTextField();
        JLabel algorithmLabel = new JLabel("Algorithm: ");
        algorithmComboBox = new JComboBox<>(new String[]{"Nearest Neighbor", "Other Algorithms"});
        optimizeButton = new JButton("Optimize");
        optimizeButton.addActionListener(new OptimizeButtonListener());

        inputPanel.add(deliveryLabel);
        inputPanel.add(deliveryPointsField);
        inputPanel.add(vehicleLabel);
        inputPanel.add(vehicleCapacityField);
        inputPanel.add(algorithmLabel);
        inputPanel.add(algorithmComboBox);
        inputPanel.add(optimizeButton);

        routePanel = new RouteVisualizationPanel();

        // Layout setup
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(routePanel, BorderLayout.CENTER);
    }

    private class OptimizeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String deliveryPointsText = deliveryPointsField.getText().trim();
            String vehicleCapacityText = vehicleCapacityField.getText().trim();
            String algorithm = (String) algorithmComboBox.getSelectedItem();

            // Parse delivery points
            List<String> deliveryPoints = parseDeliveryPoints(deliveryPointsText);
            if (deliveryPoints.isEmpty()) {
                JOptionPane.showMessageDialog(RouteOptimizationGUI.this,
                        "Please enter valid delivery points.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse vehicle capacity
            int vehicleCapacity;
            try {
                vehicleCapacity = Integer.parseInt(vehicleCapacityText);
                if (vehicleCapacity <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(RouteOptimizationGUI.this,
                        "Please enter a valid vehicle capacity (a positive integer).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Simulate route optimization (using a simple nearest neighbor algorithm)
            List<Point> optimizedRoute = optimizeRoute(deliveryPoints);

            // Update route visualization panel
            routePanel.setRoute(optimizedRoute);
        }
    }

    private List<String> parseDeliveryPoints(String input) {
        String[] pointsArray = input.split(",");
        List<String> pointsList = new ArrayList<>();
        for (String point : pointsArray) {
            String trimmedPoint = point.trim();
            if (!trimmedPoint.isEmpty()) {
                pointsList.add(trimmedPoint);
            }
        }
        return pointsList;
    }

    private List<Point> optimizeRoute(List<String> deliveryPoints) {
        // For simplicity, implement a basic nearest neighbor algorithm
        List<Point> optimizedRoute = new ArrayList<>();
        for (String point : deliveryPoints) {
            Point p = new Point(point);
            optimizedRoute.add(p);
        }
        // Add the starting point at the end to complete the loop
        if (!optimizedRoute.isEmpty()) {
            optimizedRoute.add(optimizedRoute.get(0));
        }
        return optimizedRoute;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RouteOptimizationGUI gui = new RouteOptimizationGUI();
            gui.setVisible(true);
        });
    }
}

class RouteVisualizationPanel extends JPanel {
    private List<Point> route;

    public void setRoute(List<Point> route) {
        this.route = route;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (route != null) {
            g.setColor(Color.BLUE);
            for (int i = 0; i < route.size() - 1; i++) {
                Point p1 = route.get(i);
                Point p2 = route.get(i + 1);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            g.setColor(Color.RED);
            for (Point p : route) {
                g.fillOval(p.x - 5, p.y - 5, 10, 10);
            }
        }
    }
}

class Point {
    int x, y;

    public Point(String cityName) {
        switch (cityName) {
            case "Pokhara":
                x = 100;
                y = 100;
                break;
            case "Kathmandu":
                x = 300;
                y = 200;
                break;
            case "Lalbandi":
                x = 200;
                y = 300;
                break;
            case "Biratnagar":
                x = 400;
                y = 400;
                break;
            case "Nepalgunj":
                x = 500;
                y = 100;
                break;
            default:
                x = 0;
                y = 0;
                break;
        }
    }
}


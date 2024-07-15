//question number 7 solutions...

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
            routePanel.setNodes(getCityPoints());
            routePanel.setConnections(getCityConnections(deliveryPoints));
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

    private List<Point> getCityPoints() {
        // Create points for each city
        List<Point> cities = new ArrayList<>();
        cities.add(new Point("Pokhara", 100, 100));
        cities.add(new Point("Kathmandu", 300, 200));
        cities.add(new Point("Lalbandi", 200, 300));
        cities.add(new Point("Biratnagar", 400, 400));
        cities.add(new Point("Nepalgunj", 500, 100));
        return cities;
    }

    private List<Connection> getCityConnections(List<String> deliveryPoints) {
        // Define connections between cities based on input delivery points
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < deliveryPoints.size(); i++) {
            String city1 = deliveryPoints.get(i);
            String city2 = deliveryPoints.get((i + 1) % deliveryPoints.size()); // Wrap around to the first city
            connections.add(new Connection(city1, city2));
        }
        return connections;
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
    private List<Point> nodes;
    private List<Connection> connections;

    public void setRoute(List<Point> route) {
        this.route = route;
        repaint();
    }

    public void setNodes(List<Point> nodes) {
        this.nodes = nodes;
        repaint();
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (nodes != null) {
            for (Point node : nodes) {
                g.setColor(Color.RED);
                g.fillOval(node.x - 5, node.y - 5, 10, 10);
                g.setColor(Color.BLACK);
                g.drawString(node.name, node.x + 10, node.y);
            }
        }

        if (connections != null) {
            g.setColor(Color.GRAY);
            for (Connection connection : connections) {
                Point city1 = findPointByName(connection.city1);
                Point city2 = findPointByName(connection.city2);
                g.drawLine(city1.x, city1.y, city2.x, city2.y);
            }
        }

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

    private Point findPointByName(String cityName) {
        for (Point node : nodes) {
            if (node.name.equals(cityName)) {
                return node;
            }
        }
        return null;
    }
}

class Point {
    int x, y;
    String name;

    public Point(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Point(String point) {
    }

    @Override
    public String toString() {
        return name;
    }
}

class Connection {
    String city1, city2;

    public Connection(String city1, String city2) {
        this.city1 = city1;
        this.city2 = city2;
    }
}

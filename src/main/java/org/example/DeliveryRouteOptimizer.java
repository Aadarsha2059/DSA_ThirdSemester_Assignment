
//Question Number 7 solutions...


package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class DeliveryRouteOptimizer extends JFrame {
    private JTextArea deliveryListArea;
    private JComboBox<String> algorithmComboBox;
    private JTextField vehicleCapacityField;
    private JTextField drivingDistanceField;
    private JButton optimizeButton;
    private JTextArea routeDisplayArea;
    private MapPanel mapPanel;

    public DeliveryRouteOptimizer() {
        setTitle("Delivery Route Optimization");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Delivery List Area
        deliveryListArea = new JTextArea(10, 30);
        deliveryListArea.setBorder(BorderFactory.createTitledBorder("Delivery List (Address, Priority)"));
        JScrollPane deliveryListScrollPane = new JScrollPane(deliveryListArea);

        // Algorithm Selection
        algorithmComboBox = new JComboBox<>(new String[]{"Dijkstra's Algorithm"});

        // Vehicle Options
        JPanel vehiclePanel = new JPanel();
        vehiclePanel.setBorder(BorderFactory.createTitledBorder("Vehicle Options"));
        vehiclePanel.setLayout(new GridLayout(2, 2));
        vehiclePanel.add(new JLabel("Vehicle Capacity:"));
        vehicleCapacityField = new JTextField();
        vehiclePanel.add(vehicleCapacityField);
        vehiclePanel.add(new JLabel("Driving Distance Limit:"));
        drivingDistanceField = new JTextField();
        vehiclePanel.add(drivingDistanceField);

        // Optimize Button
        optimizeButton = new JButton("Optimize Route");
        optimizeButton.addActionListener(new OptimizeButtonListener());

        // Route Display Area
        routeDisplayArea = new JTextArea(10, 30);
        routeDisplayArea.setBorder(BorderFactory.createTitledBorder("Optimized Route"));
        routeDisplayArea.setEditable(false);
        JScrollPane routeDisplayScrollPane = new JScrollPane(routeDisplayArea);

        // Map Panel
        mapPanel = new MapPanel();

        // Layout Setup
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(deliveryListScrollPane, BorderLayout.CENTER);
        inputPanel.add(vehiclePanel, BorderLayout.SOUTH);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(new JLabel("Select Algorithm:"), BorderLayout.WEST);
        controlPanel.add(algorithmComboBox, BorderLayout.CENTER);
        controlPanel.add(optimizeButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(routeDisplayScrollPane, BorderLayout.EAST);
        add(mapPanel, BorderLayout.SOUTH);
    }

    private class OptimizeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] deliveries = deliveryListArea.getText().split("\n");
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            int vehicleCapacity = Integer.parseInt(vehicleCapacityField.getText());
            int drivingDistanceLimit = Integer.parseInt(drivingDistanceField.getText());

            List<DeliveryPoint> deliveryPoints = new ArrayList<>();
            for (String delivery : deliveries) {
                String[] parts = delivery.split(",");
                deliveryPoints.add(new DeliveryPoint(parts[0].trim(), Integer.parseInt(parts[1].trim())));
            }

            RouteOptimizer optimizer = new RouteOptimizer(deliveryPoints, vehicleCapacity, drivingDistanceLimit);
            List<DeliveryPoint> optimizedRoute = optimizer.optimize(algorithm);

            routeDisplayArea.setText("Optimized Route:\n");
            for (DeliveryPoint point : optimizedRoute) {
                routeDisplayArea.append(point.address + " (Priority: " + point.priority + ")\n");
            }

            mapPanel.updateRoute(optimizedRoute);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DeliveryRouteOptimizer gui = new DeliveryRouteOptimizer();
            gui.setVisible(true);
        });
    }
}

class DeliveryPoint {
    String address;
    int priority;

    public DeliveryPoint(String address, int priority) {
        this.address = address;
        this.priority = priority;
    }
}

class RouteOptimizer {
    private List<DeliveryPoint> deliveryPoints;
    private int vehicleCapacity;
    private int drivingDistanceLimit;

    public RouteOptimizer(List<DeliveryPoint> deliveryPoints, int vehicleCapacity, int drivingDistanceLimit) {
        this.deliveryPoints = deliveryPoints;
        this.vehicleCapacity = vehicleCapacity;
        this.drivingDistanceLimit = drivingDistanceLimit;
    }

    public List<DeliveryPoint> optimize(String algorithm) {
        if ("Dijkstra's Algorithm".equals(algorithm)) {
            return dijkstraAlgorithm();
        }
        // Implement other algorithms if needed
        return new ArrayList<>();
    }

    private List<DeliveryPoint> dijkstraAlgorithm() {
        // Sort delivery points by priority for simplicity
        deliveryPoints.sort(Comparator.comparingInt(dp -> dp.priority));
        return new ArrayList<>(deliveryPoints);
    }
}

class MapPanel extends JPanel {
    private List<DeliveryPoint> route;

    public MapPanel() {
        this.route = new ArrayList<>();
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.WHITE);
    }

    public void updateRoute(List<DeliveryPoint> route) {
        this.route = route;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (route != null && !route.isEmpty()) {
            int x = 50, y = 50;
            for (DeliveryPoint point : route) {
                g.setColor(Color.BLUE);
                g.fillOval(x, y, 10, 10);
                g.setColor(Color.BLACK);
                g.drawString(point.address, x + 15, y + 5);
                y += 50;
            }
        }
    }
}

// executin above codes for the given conditions in the question I developed a delivery route GUI...

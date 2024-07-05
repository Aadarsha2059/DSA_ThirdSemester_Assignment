package org.example;

import java.util.*;

public class TSPHillClimbing {

    // Define the number of cities and the distance matrix
    private static final int NUM_CITIES = 5;
    private int[][] distanceMatrix;

    // Constructor to initialize the distance matrix
    public TSPHillClimbing(int[][] distances) {
        if (distances.length != NUM_CITIES || distances[0].length != NUM_CITIES) {
            throw new IllegalArgumentException("Invalid distance matrix dimensions.");
        }
        this.distanceMatrix = distances;
    }

    // Method to perform the Hill Climbing optimization for TSP
    public List<Integer> solveTSP() {
        // Start with a random initial tour
        List<Integer> currentTour = generateRandomTour();
        int currentDistance = calculateTourDistance(currentTour);

        boolean improved;
        do {
            improved = false;

            // Generate all possible neighboring tours by swapping cities
            for (int i = 0; i < NUM_CITIES - 1; i++) {
                for (int j = i + 1; j < NUM_CITIES; j++) {
                    // Create a new tour by swapping cities i and j
                    List<Integer> neighborTour = new ArrayList<>(currentTour);
                    Collections.swap(neighborTour, i, j);

                    // Calculate the distance of the new tour
                    int neighborDistance = calculateTourDistance(neighborTour);

                    // If the new tour is shorter, accept it
                    if (neighborDistance < currentDistance) {
                        currentTour = neighborTour;
                        currentDistance = neighborDistance;
                        improved = true;
                    }
                }
            }
        } while (improved);

        return currentTour;
    }

    // Method to generate a random initial tour
    private List<Integer> generateRandomTour() {
        List<Integer> tour = new ArrayList<>();
        for (int i = 0; i < NUM_CITIES; i++) {
            tour.add(i);
        }
        Collections.shuffle(tour);
        return tour;
    }

    // Method to calculate the total distance of a tour
    private int calculateTourDistance(List<Integer> tour) {
        int totalDistance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int city1 = tour.get(i);
            int city2 = tour.get(i + 1);
            totalDistance += distanceMatrix[city1][city2];
        }
        // Add distance back to the starting city
        int lastCity = tour.get(tour.size() - 1);
        int firstCity = tour.get(0);
        totalDistance += distanceMatrix[lastCity][firstCity];
        return totalDistance;
    }

    public static void main(String[] args) {
        // Example distance matrix for 5 cities
        int[][] distances = {
                {0, 10, 15, 20, 25},
                {10, 0, 35, 25, 30},
                {15, 35, 0, 30, 20},
                {20, 25, 30, 0, 10},
                {25, 30, 20, 10, 0}
        };

        TSPHillClimbing tspSolver = new TSPHillClimbing(distances);
        List<Integer> solution = tspSolver.solveTSP();

        System.out.println("Optimal Tour: " + solution);
        System.out.println("Optimal Distance: " + tspSolver.calculateTourDistance(solution));
    }
}

//from the above solutions we can get
//Optimal Tour: [4, 2, 0, 1, 3]
//Optimal Distance: 80
package org.example;

import java.util.HashMap;
import java.util.Map;

public class MovieTheaterSeating {

    public static boolean canSitTogether(int[] seatingChart, int indexDiff, int valueDiff) {
        // Map to keep track of the last occurrence of each seat number
        Map<Integer, Integer> seatMap = new HashMap<>();

        for (int i = 0; i < seatingChart.length; i++) {
            int currentSeat = seatingChart[i];

            // Check for possible friends within the allowed seat difference
            for (int diff = -valueDiff; diff <= valueDiff; diff++) {
                int potentialFriendSeat = currentSeat + diff;
                if (seatMap.containsKey(potentialFriendSeat)) {
                    int friendIndex = seatMap.get(potentialFriendSeat);
                    if (Math.abs(i - friendIndex) <= indexDiff) {
                        return true;
                    }
                }
            }

            // Update the map with the current seat and index
            seatMap.put(currentSeat, i);
        }

        // No suitable pairs found
        return false;
    }

    public static void main(String[] args) {
        // Example seating chart
        int[] seatingChart = {2, 3, 5, 4, 9};
        int indexDiff = 2;
        int valueDiff = 1;

        boolean result = canSitTogether(seatingChart, indexDiff, valueDiff);
        System.out.println("Can two friends sit together? " + result);
    }
}


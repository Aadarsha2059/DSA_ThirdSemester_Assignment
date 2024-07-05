//Question 1 (a) solutions

package org.example;

import java.util.*;

class ClassroomScheduler {
    public static int mostUsedClassroom(int n, int[][] classes) {
        // Step 1: Sort classes by start time and by size (in descending order) if they have the same start time.
        Arrays.sort(classes, (a, b) -> {
            if (a[0] != b[0]) return a[0] - b[0];
            return b[1] - a[1];
        });

        // Step 2: Create a priority queue to keep track of available rooms.
        // The queue holds the end time of the current class in that room.
        PriorityQueue<int[]> roomQueue = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        // Step 3: Create an array to count the usage of each room.
        int[] roomUsage = new int[n];

        for (int i = 0; i < n; i++) {
            roomQueue.offer(new int[]{0, i});
        }

        for (int[] cls : classes) {
            int start = cls[0], end = cls[1];

            // Step 4: Release rooms that are free before the current class starts.
            while (!roomQueue.isEmpty() && roomQueue.peek()[0] <= start) {
                roomQueue.poll();
            }

            // Step 5: If no room is available, we delay the class until the next available room is free.
            int[] earliestRoom = roomQueue.poll();
            if (earliestRoom == null || earliestRoom[0] > start) {
                if (earliestRoom != null) {
                    start = earliestRoom[0];
                    end = start + (cls[1] - cls[0]);
                }
                roomQueue.offer(new int[]{end, earliestRoom == null ? 0 : earliestRoom[1]});
                roomUsage[earliestRoom == null ? 0 : earliestRoom[1]]++;
            } else {
                // Step 6: Assign the class to the earliest available room.
                roomQueue.offer(new int[]{end, earliestRoom[1]});
                roomUsage[earliestRoom[1]]++;
            }
        }

        // Step 7: Determine which room was used the most.
        int maxUsage = 0, mostUsedRoom = 0;
        for (int i = 0; i < n; i++) {
            if (roomUsage[i] > maxUsage || (roomUsage[i] == maxUsage && i < mostUsedRoom)) {
                maxUsage = roomUsage[i];
                mostUsedRoom = i;
            }
        }

        return mostUsedRoom;
    }

    public static void main(String[] args) {
        int n = 2;
        int[][] classes = {
                {0, 10},
                {1, 5},
                {2, 7},
                {3, 4}
        };
        System.out.println(mostUsedClassroom(n, classes));
    }
}

//output we got for this is 0
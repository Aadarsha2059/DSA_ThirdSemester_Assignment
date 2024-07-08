
//Question number 5 B solutions...


package org.example;



public class LongestHike {

    public static void main(String[] args) {
        int[] trail = {4, 2, 1, 4, 3, 4, 5, 8, 15};
        int k = 3;
        int result = findLongestHike(trail, k);
        System.out.println(result); // Output: 5
    }

    public static int findLongestHike(int[] nums, int k) {
        int n = nums.length;
        int maxLength = 0;
        int start = 0;

        for (int end = 1; end < n; end++) {
            if (nums[end] - nums[end - 1] > k) {
                // Move the start pointer to the current end position
                start = end;
            }
            // Update the maximum length of the valid hiking trail
            maxLength = Math.max(maxLength, end - start + 1);
        }

        return maxLength;
    }
}

// Here, executing the given conditon in the question I got the output i.e
//8 here

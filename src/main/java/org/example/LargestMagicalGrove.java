
//Question 4 B solutions ...

package org.example;



public class LargestMagicalGrove {

    // Definition for a binary tree node.
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    static class Result {
        boolean isBST;
        int sum;
        int min;
        int max;

        Result(boolean isBST, int sum, int min, int max) {
            this.isBST = isBST;
            this.sum = sum;
            this.min = min;
            this.max = max;
        }
    }

    private static int maxSum = 0;

    public static void main(String[] args) {
        // Example tree: [1,4,3,2,4,2,5,null,null,null,null,null,null,4,6]
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(4);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(2);
        root.right.right = new TreeNode(5);
        root.right.right.left = new TreeNode(4);
        root.right.right.right = new TreeNode(6);

        int result = findLargestMagicalGrove(root);
        System.out.println(result); // Output: 20
    }

    public static int findLargestMagicalGrove(TreeNode root) {
        maxSum = 0;
        postOrderTraversal(root);
        return maxSum;
    }

    private static Result postOrderTraversal(TreeNode node) {
        if (node == null) {
            return new Result(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        Result left = postOrderTraversal(node.left);
        Result right = postOrderTraversal(node.right);

        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int currentSum = node.val + left.sum + right.sum;
            maxSum = Math.max(maxSum, currentSum);
            int currentMin = Math.min(node.val, left.min);
            int currentMax = Math.max(node.val, right.max);
            return new Result(true, currentSum, currentMin, currentMax);
        } else {
            return new Result(false, 0, 0, 0);
        }
    }
}

//Executing the above conditions in the asked question I observed output
//20

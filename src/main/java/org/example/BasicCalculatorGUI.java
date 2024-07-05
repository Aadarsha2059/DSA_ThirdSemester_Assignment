package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;
import java.util.Stack;

public class BasicCalculatorGUI extends JFrame {

    private JTextField inputField;
    private JButton calculateButton;
    private JLabel resultLabel;

    public BasicCalculatorGUI() {
        // Setup JFrame
        setTitle("Basic Calculator");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Initialize components
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(300, 30));

        calculateButton = new JButton("Calculate");
        resultLabel = new JLabel("Result:");

        // Add components to the JFrame
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(new JLabel("Enter Expression:"));
        inputPanel.add(inputField);
        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(calculateButton);
        add(buttonPanel, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resultPanel.add(resultLabel);
        add(resultPanel, BorderLayout.SOUTH);

        // ActionListener for Calculate button
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (!input.isEmpty()) {
                    try {
                        String result = evaluateExpression(input);
                        resultLabel.setText("Result: " + result);
                    } catch (IllegalArgumentException ex) {
                        resultLabel.setText("Result: Error - " + ex.getMessage());
                    }
                } else {
                    resultLabel.setText("Result: No input provided.");
                }
            }
        });
    }

    // Method to evaluate mathematical expression
    private String evaluateExpression(String expression) {
        try {
            Stack<Integer> operands = new Stack<>();
            Stack<Character> operators = new Stack<>();

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                // Ignore spaces
                if (c == ' ')
                    continue;

                // If digit, push it to operands stack
                if (Character.isDigit(c)) {
                    int num = 0;
                    while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                        num = num * 10 + (expression.charAt(i) - '0');
                        i++;
                    }
                    i--;
                    operands.push(num);
                }
                // If opening bracket, push it to operators stack
                else if (c == '(') {
                    operators.push(c);
                }
                // If closing bracket, solve entire bracket
                else if (c == ')') {
                    while (operators.peek() != '(') {
                        int output = performOperation(operators.pop(), operands.pop(), operands.pop());
                        operands.push(output);
                    }
                    operators.pop();
                }
                // If operator, perform operation
                else if (isOperator(c)) {
                    while (!operators.isEmpty() && precedence(c) <= precedence(operators.peek())) {
                        int output = performOperation(operators.pop(), operands.pop(), operands.pop());
                        operands.push(output);
                    }
                    operators.push(c);
                }
                // Invalid character
                else {
                    throw new IllegalArgumentException("Invalid character in expression.");
                }
            }
            // Remaining elements in stacks
            while (!operators.isEmpty()) {
                int output = performOperation(operators.pop(), operands.pop(), operands.pop());
                operands.push(output);
            }
            return operands.pop().toString();
        } catch (EmptyStackException | ArithmeticException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid expression.");
        }
    }

    // Method to perform arithmetic operation
    private int performOperation(char operator, int b, int a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException("Division by zero.");
                return a / b;
        }
        throw new IllegalArgumentException("Invalid operator.");
    }

    // Method to check if character is operator
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    // Method to return precedence of operator
    private int precedence(char c) {
        switch (c) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BasicCalculatorGUI calculator = new BasicCalculatorGUI();
                calculator.setLocationRelativeTo(null); // Center the window
                calculator.setVisible(true);
            }
        });
    }
}

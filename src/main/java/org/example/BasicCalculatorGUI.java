//2 a solutions...

package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class BasicCalculatorGUI extends JFrame {

    private JTextField inputField;
    private JButton[] buttons;
    private JLabel resultLabel;

    public BasicCalculatorGUI() {
        // Set up the frame
        setTitle("Basic Calculator");
        setSize(300, 400); // Adjusted size for better calculator layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input field
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 24));
        inputField.setHorizontalAlignment(JTextField.RIGHT);
        inputField.setEditable(false); // Disable direct input
        add(inputField, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5)); // Rows, Columns, Horizontal Gap, Vertical Gap
        buttons = new JButton[20]; // Array for 20 buttons (0-9, +, -, *, /, =, C, (, ))

        // Button labels
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "(", ")", "+",
                "C", "=", ".", " "
        };

        // Add buttons to panel with color decorations
        for (int i = 0; i < 20; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 18));
            buttons[i].setFocusPainted(false); // Remove focus border

            // Set colors for digits and operators
            if (buttonLabels[i].matches("[0-9]")) {
                buttons[i].setForeground(Color.BLUE);
            } else if (buttonLabels[i].matches("[+\\-*/()]")) {
                buttons[i].setForeground(Color.RED);
            }

            buttons[i].addActionListener(new ButtonClickListener());
            buttonPanel.add(buttons[i]);
        }

        // Add button panel to center
        add(buttonPanel, BorderLayout.CENTER);

        // Result label
        resultLabel = new JLabel("Result: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultLabel.setHorizontalAlignment(JLabel.RIGHT);
        add(resultLabel, BorderLayout.SOUTH);

        // Make the frame visible
        setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String buttonText = button.getText();

            if (buttonText.equals("=")) {
                String expression = inputField.getText();
                try {
                    int result = evaluate(expression);
                    resultLabel.setText("Result: " + result);
                } catch (Exception ex) {
                    resultLabel.setText("Error: Invalid Expression");
                }
            } else if (buttonText.equals("C")) {
                inputField.setText("");
                resultLabel.setText("Result: ");
            } else {
                inputField.setText(inputField.getText() + buttonText);
            }
        }
    }

    private int evaluate(String expression) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == ' ') continue;
            if (ch >= '0' && ch <= '9') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && expression.charAt(i) >= '0' && expression.charAt(i) <= '9') {
                    sb.append(expression.charAt(i++));
                }
                values.push(Integer.parseInt(sb.toString()));
                i--;
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && hasPrecedence(ch, operators.peek())) {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(ch);
            }
        }
        while (!operators.isEmpty()) {
            values.push(applyOp(operators.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BasicCalculatorGUI::new);
    }
}

// 2+2+2+2
//output=8

// 1/0
//output=Invalid expression

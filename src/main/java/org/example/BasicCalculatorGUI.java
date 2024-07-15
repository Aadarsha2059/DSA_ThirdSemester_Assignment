// question number 2 a solutions

package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class BasicCalculatorGUI extends JFrame {

    private JTextField inputField;
    private JButton calculateButton;
    private JLabel resultLabel;

    public BasicCalculatorGUI() {
        // Set up the frame
        setTitle("Basic Calculator");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        // Input field
        inputField = new JTextField();
        add(inputField);

        // Calculate button
        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new CalculateButtonListener());
        add(calculateButton);

        // Result label
        resultLabel = new JLabel("Result: ");
        add(resultLabel);

        // Make the frame visible
        setVisible(true);
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String expression = inputField.getText();
            try {
                int result = evaluate(expression);
                resultLabel.setText("Result: " + result);
            } catch (Exception ex) {
                resultLabel.setText("Error: Invalid Expression");
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


//input (1+(4+5+2)-3)+(6+11)
//output: 26

//(1+(4+50+2)-3)+(6+11) as input
// output:71
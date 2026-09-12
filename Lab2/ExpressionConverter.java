package Lab2;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class ExpressionConverter {

    static HashMap<Character, Integer> precedence = new HashMap<>();
    static Scanner sc = new Scanner(System.in);

    static boolean isOperand(char ch) {
        return Character.isLetterOrDigit(ch);
    }

    static void defineOperators() {
        System.out.print("Number of operators: ");
        int n = Integer.parseInt(sc.nextLine().trim());

        for (int i = 0; i < n; i++) {
            System.out.print("Operator: ");
            char op = sc.nextLine().trim().charAt(0);
            System.out.print("Precedence: ");
            int prec = Integer.parseInt(sc.nextLine().trim());
            precedence.put(op, prec);
        }
        System.out.println("Operators saved.");
    }

    static void displayTable() {
        if (precedence.isEmpty()) {
            System.out.println("No operators defined yet.");
            return;
        }
        System.out.println("Operator Precedence Table:");
        for (char op : precedence.keySet()) {
            System.out.println(op + " -> " + precedence.get(op));
        }
    }

    static boolean balancedParentheses(String expr) {
        int balance = 0;
        for (char ch : expr.toCharArray()) {
            if (ch == '(') balance++;
            if (ch == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }

    static void infixToPostfix() {
        if (precedence.isEmpty()) {
            System.out.println("Define operators first (option 1).");
            return;
        }

        System.out.print("Enter infix expression: ");
        String infix = sc.nextLine().replaceAll("\\s", "");

        if (!balancedParentheses(infix)) {
            System.out.println("Error: mismatched parentheses.");
            return;
        }

        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();
        boolean error = false;

        System.out.printf("%-8s %-10s %-15s %-15s%n", "Symbol", "Action", "Stack", "Postfix");

        for (char ch : infix.toCharArray()) {
            if (isOperand(ch)) {
                postfix.append(ch);
                System.out.printf("%-8c %-10s %-15s %-15s%n", ch, "Operand", stack, postfix);
            } else if (ch == '(') {
                stack.push(ch);
                System.out.printf("%-8c %-10s %-15s %-15s%n", ch, "Push (", stack, postfix);
            } else if (ch == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                System.out.printf("%-8c %-10s %-15s %-15s%n", ch, "Pop till (", stack, postfix);
            } else if (precedence.containsKey(ch)) {
                while (!stack.isEmpty() && stack.peek() != '('
                        && precedence.containsKey(stack.peek())
                        && precedence.get(ch) <= precedence.get(stack.peek())) {
                    postfix.append(stack.pop());
                }
                stack.push(ch);
                System.out.printf("%-8c %-10s %-15s %-15s%n", ch, "Push op", stack, postfix);
            } else {
                System.out.println("Error: unknown operator '" + ch + "'");
                error = true;
                break;
            }
        }

        if (error) return;

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        System.out.printf("%-8s %-10s %-15s %-15s%n", "End", "Pop rest", stack, postfix);

        System.out.println();
        System.out.println("Infix   : " + infix);
        System.out.println("Postfix : " + postfix);
    }

    static void postfixToInfix() {
        System.out.print("Enter postfix expression: ");
        String postfix = sc.nextLine().replaceAll("\\s", "");

        Stack<String> stack = new Stack<>();
        boolean error = false;

        for (char ch : postfix.toCharArray()) {
            if (isOperand(ch)) {
                stack.push(String.valueOf(ch));
            } else if (precedence.containsKey(ch) || "+-*/^".indexOf(ch) >= 0) {
                if (stack.size() < 2) {
                    System.out.println("Error: invalid postfix expression.");
                    error = true;
                    break;
                }
                String right = stack.pop();
                String left = stack.pop();
                stack.push("(" + left + ch + right + ")");
            } else {
                System.out.println("Error: unknown operator '" + ch + "'");
                error = true;
                break;
            }
        }

        if (error || stack.isEmpty()) return;

        String result = stack.pop();
        if (result.startsWith("(") && result.endsWith(")")) {
            result = result.substring(1, result.length() - 1);
        }

        System.out.println();
        System.out.println("Postfix : " + postfix);
        System.out.println("Infix   : " + result);
    }

    public static void main(String[] args) {
        int choice = -1;

        while (choice != 5) {
            System.out.println();
            System.out.println("1. Define Operators and Precedence");
            System.out.println("2. Infix to Postfix");
            System.out.println("3. Postfix to Infix");
            System.out.println("4. Display Operator Table");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1: defineOperators(); break;
                case 2: infixToPostfix(); break;
                case 3: postfixToInfix(); break;
                case 4: displayTable(); break;
                case 5: System.out.println("Exiting."); break;
                default: System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }
}
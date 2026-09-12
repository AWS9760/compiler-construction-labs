package Lab2;

import java.util.Scanner;
import java.util.Stack;

public class InfixToPostfix {

    static int precedence(char op) {
        switch (op) {
            case '+':
            case '-': return 1;
            case '*':
            case '/': return 2;
            case '^': return 3;
            default: return -1;
        }
    }

    static boolean isOperand(char ch) {
        return Character.isLetterOrDigit(ch);
    }

    static String convert(String infix) {
        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();

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
            } else if (precedence(ch) > 0) {
                while (!stack.isEmpty() && stack.peek() != '(' && precedence(ch) <= precedence(stack.peek())) {
                    postfix.append(stack.pop());
                }
                stack.push(ch);
                System.out.printf("%-8c %-10s %-15s %-15s%n", ch, "Push op", stack, postfix);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        System.out.printf("%-8s %-10s %-15s %-15s%n", "End", "Pop rest", stack, postfix);

        return postfix.toString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter infix expression: ");
        String infix = sc.nextLine().replaceAll("\\s", "");

        String postfix = convert(infix);

        System.out.println();
        System.out.println("Infix   : " + infix);
        System.out.println("Postfix : " + postfix);

        sc.close();
    }
}
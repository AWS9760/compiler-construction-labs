package Lab2;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class UserDefinedPrecedence {

    static boolean isOperand(char ch) {
        return Character.isLetterOrDigit(ch);
    }

    static String convert(String infix, HashMap<Character, Integer> precedence) {
        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();

        for (char ch : infix.toCharArray()) {
            if (isOperand(ch)) {
                postfix.append(ch);
            } else if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (precedence.containsKey(ch)) {
                while (!stack.isEmpty() && stack.peek() != '('
                        && precedence.containsKey(stack.peek())
                        && precedence.get(ch) <= precedence.get(stack.peek())) {
                    postfix.append(stack.pop());
                }
                stack.push(ch);
            } else {
                System.out.println("Unknown operator: " + ch);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }

        return postfix.toString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<Character, Integer> precedence = new HashMap<>();

        System.out.print("Number of operators: ");
        int n = Integer.parseInt(sc.nextLine().trim());

        for (int i = 0; i < n; i++) {
            System.out.print("Operator: ");
            char op = sc.nextLine().trim().charAt(0);
            System.out.print("Precedence: ");
            int prec = Integer.parseInt(sc.nextLine().trim());
            precedence.put(op, prec);
        }

        System.out.print("Enter infix expression: ");
        String infix = sc.nextLine().replaceAll("\\s", "");

        String postfix = convert(infix, precedence);

        System.out.println();
        System.out.println("Operator Precedence Table:");
        for (char op : precedence.keySet()) {
            System.out.println(op + " -> " + precedence.get(op));
        }

        System.out.println();
        System.out.println("Infix   : " + infix);
        System.out.println("Postfix : " + postfix);

        sc.close();
    }
}
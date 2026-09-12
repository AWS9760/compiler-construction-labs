package Lab2;

import java.util.Scanner;
import java.util.Stack;

public class PostfixToInfix {

    static boolean isOperand(char ch) {
        return Character.isLetterOrDigit(ch);
    }

    static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
    }

    static String convert(String postfix) {
        Stack<String> stack = new Stack<>();

        for (char ch : postfix.toCharArray()) {
            if (isOperand(ch)) {
                stack.push(String.valueOf(ch));
            } else if (isOperator(ch)) {
                String right = stack.pop();
                String left = stack.pop();
                stack.push("(" + left + ch + right + ")");
            }
        }

        String result = stack.pop();
        if (result.startsWith("(") && result.endsWith(")")) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter postfix expression: ");
        String postfix = sc.nextLine().replaceAll("\\s", "");

        String infix = convert(postfix);

        System.out.println();
        System.out.println("Postfix : " + postfix);
        System.out.println("Infix   : " + infix);

        sc.close();
    }
}
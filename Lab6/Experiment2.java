import java.util.*;

public class Experiment2 {

    // Stores the input tokens
    static List<String> tokens;

    // Points to the current token
    static int index = 0;

    // Returns the current token
    static String currentToken() {
        if (index < tokens.size()) {
            return tokens.get(index);
        }
        return "$";
    }

    // Matches the expected token
    static void match(String expected) {
        if (currentToken().equals(expected)) {
            System.out.println("Matched: " + expected);
            index++;
        } else {
            error("Expected '" + expected
                    + "' but found '" + currentToken() + "'");
        }
    }

    // Displays a syntax error
    static void error(String message) {
        throw new RuntimeException("Syntax Error: " + message);
    }

    // E -> T E'
    static void E() {
        System.out.println("E()");
        T();
        EPrime();
    }

    // E' -> + T E' | - T E' | ε
    static void EPrime() {
        System.out.println("EPrime()");
        if (currentToken().equals("+")) {
            match("+");
            T();
            EPrime();
        }
        else if (currentToken().equals("-")) {
            match("-");
            T();
            EPrime();
        }
        // Otherwise choose ε
    }

    // T -> F T'
    static void T() {
        System.out.println("T()");
        F();
        TPrime();
    }

    // T' -> * F T' | / F T' | ε
    static void TPrime() {
        System.out.println("TPrime()");
        if (currentToken().equals("*")) {
            match("*");
            F();
            TPrime();
        }
        else if (currentToken().equals("/")) {
            match("/");
            F();
            TPrime();
        }
        // Otherwise choose ε
    }

    // F -> ( E ) | id
    static void F() {
        System.out.println("F()");
        if (currentToken().equals("id")) {
            match("id");
        }
        else if (currentToken().equals("(")) {
            match("(");
            E();
            match(")");
        }
        else {
            error("Expected 'id' or '(' but found '"
                    + currentToken() + "'");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Recursive Descent Parser");
        System.out.print("Enter tokens separated by spaces: ");

        String input = sc.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Syntax Error: Empty input");
            sc.close();
            return;
        }

        // Convert input into tokens
        tokens = new ArrayList<>(Arrays.asList(input.split("\\s+")));

        try {
            // Start parsing from E
            E();

            // Check that all tokens were consumed
            if (index == tokens.size()) {
                System.out.println("\nResult: Valid Expression");
            }
            else {
                error("Unexpected token '" + currentToken() + "'");
            }
        } catch (RuntimeException e) {
            System.out.println("\n" + e.getMessage());
        }

        sc.close();
    }
}
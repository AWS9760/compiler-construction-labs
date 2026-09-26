import java.util.*;

public class Task1 {

    static class SyntaxError extends RuntimeException {
        final int at;   // 0-based character position
        SyntaxError(String message, int at) { super(message); this.at = at; }
    }

    static String input;
    static int pos;
    static int depth;
    static boolean trace = true;

    static char current() {
        return pos < input.length() ? input.charAt(pos) : '$';
    }

    static String describe() {
        if (pos < input.length()) return "'" + input.charAt(pos) + "' at position " + (pos + 1);
        return "end of input";
    }

    static void log(String msg) {
        if (trace) System.out.println("  ".repeat(depth) + msg);
    }

    static void match(char expected) {
        if (current() == expected) {
            log("Matched: " + expected + "  (position " + (pos + 1) + ")");
            pos++;
        } else {
            throw new SyntaxError("expected '" + expected + "' but found " + describe(), pos);
        }
    }

    // S -> ( S ) S | ε
    static void S() {
        log("S()"); depth++;
        if (current() == '(') {
            int open = pos;
            match('(');
            S();
            if (current() != ')') {
                throw new SyntaxError("missing ')' for the '(' at position "
                        + (open + 1) + "; found " + describe(), pos);
            }
            match(')');
            S();
        }
        // otherwise choose ε
        depth--;
    }

    static String parse(String raw) {
        input = raw.replaceAll("\\s+", "");
        pos = 0;
        depth = 0;

        // only '(' and ')' are allowed
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != '(' && c != ')') {
                return report(new SyntaxError("invalid character '" + c
                        + "' at position " + (i + 1), i));
            }
        }

        try {
            S();
            // S stops only at ')' or end of input; a leftover ')' has no partner
            if (pos != input.length()) {
                throw new SyntaxError("unexpected ')' at position " + (pos + 1)
                        + " - it has no matching '('", pos);
            }
            return input.isEmpty() ? "Balanced (empty string, S -> ε)" : "Balanced";
        } catch (SyntaxError e) {
            return report(e);
        }
    }

    static String report(SyntaxError e) {
        return "NOT balanced: " + e.getMessage()
                + "\n      " + input
                + "\n      " + " ".repeat(e.at) + "^";
    }

    static void runTests(String title, String[] cases) {
        System.out.println("\n-- " + title + " --");
        for (String c : cases) {
            System.out.printf("%-9s => %s%n", c.isEmpty() ? "(empty)" : c, parse(c));
        }
    }

    public static void main(String[] args) {
        String[] accept = { "()", "(())", "()()", "((()))", "(()())" };
        String[] reject = { "(", ")", "(()", "())", ")(", "(()))" };

        System.out.println("Balanced Parentheses Parser");
        trace = false;
        runTests("Should ACCEPT", accept);
        runTests("Should REJECT", reject);

        trace = true;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== Interactive mode (blank line to quit) ===");
        while (true) {
            System.out.print("Enter parentheses: ");
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine();
            if (line.trim().isEmpty()) break;
            System.out.println();
            System.out.println("\nResult: " + parse(line) + "\n");
        }
        sc.close();
    }
}
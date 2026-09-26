import java.util.*;

public class Task2 {

    static class Token {
        final String kind;   // "id", "&", "|", "!", "(", ")", "$"
        final String text;
        final int col;       // 0-based column in the input line
        Token(String kind, String text, int col) {
            this.kind = kind; this.text = text; this.col = col;
        }
    }

    static class SyntaxError extends RuntimeException {
        final int col;
        SyntaxError(String message, int col) { super(message); this.col = col; }
    }

    static String source;
    static List<Token> tokens;
    static int index;
    static int depth;
    static boolean trace = true;

    // ---------- Lexer ----------
    static List<Token> tokenize(String s) {
        List<Token> out = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            if ("&|!()".indexOf(c) >= 0) {
                out.add(new Token(String.valueOf(c), String.valueOf(c), i));
                i++;
                continue;
            }
            if (Character.isLetter(c)) {
                int start = i;
                while (i < s.length() && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_')) i++;
                out.add(new Token("id", s.substring(start, i), start));
                continue;
            }
            throw new SyntaxError("Lexical Error: invalid character '" + c + "'", i);
        }
        out.add(new Token("$", "end of input", s.length()));   // end marker
        return out;
    }

    // ---------- Parser helpers ----------
    static Token current() { return tokens.get(index); }

    static String describe(Token t) {
        return t.kind.equals("$") ? "end of input" : "'" + t.text + "'";
    }

    static SyntaxError error(String message) {
        return new SyntaxError("Syntax Error: " + message, current().col);
    }

    static void enter(String name) { if (trace) System.out.println("  ".repeat(depth) + name); depth++; }
    static void leave() { depth--; }

    static void match(String kind) {
        if (current().kind.equals(kind)) {
            if (trace) System.out.println("  ".repeat(depth) + "Matched: " + current().text);
            index++;
        } else {
            throw error("expected '" + kind + "' but found " + describe(current()));
        }
    }

    // ---------- Grammar functions ----------

    // E -> T E'
    static String E() {
        enter("E()");
        String result = EPrime(T());
        leave();
        return result;
    }

    // E' -> | T E' | ε
    static String EPrime(String left) {
        enter("EPrime()");
        String result = left;
        if (current().kind.equals("|")) {
            match("|");
            String right = T();
            result = EPrime("(" + left + " | " + right + ")");
        }
        // otherwise ε
        leave();
        return result;
    }

    // T -> F T'
    static String T() {
        enter("T()");
        String result = TPrime(F());
        leave();
        return result;
    }

    // T' -> & F T' | ε
    static String TPrime(String left) {
        enter("TPrime()");
        String result = left;
        if (current().kind.equals("&")) {
            match("&");
            String right = F();
            result = TPrime("(" + left + " & " + right + ")");
        }
        // otherwise ε
        leave();
        return result;
    }

    // F -> ! F | ( E ) | id
    static String F() {
        enter("F()");
        String result;
        Token t = current();
        if (t.kind.equals("!")) {
            match("!");
            result = "!" + F();
        } else if (t.kind.equals("id")) {
            match("id");
            result = t.text;
        } else if (t.kind.equals("(")) {
            match("(");
            result = E();
            if (!current().kind.equals(")")) {
                throw error("missing ')' to close '(' at column " + (t.col + 1)
                        + "; found " + describe(current()));
            }
            match(")");
        } else {
            throw error("expected an identifier, '!' or '(' but found " + describe(t));
        }
        leave();
        return result;
    }

    // ---------- Driver ----------
    static String parse(String input) {
        source = input;
        if (input.trim().isEmpty()) return "Syntax Error: Empty input";
        index = 0;
        depth = 0;
        try {
            tokens = tokenize(input);
            String structure = E();
            if (!current().kind.equals("$")) {
                if (current().kind.equals(")")) throw error("unmatched ')' - there is no '(' to close");
                throw error("unexpected " + describe(current()) + " after a complete formula");
            }
            return "Valid   structure: " + structure;
        } catch (SyntaxError e) {
            return e.getMessage() + " (column " + (e.col + 1) + ")"
                    + "\n        " + source
                    + "\n        " + " ".repeat(e.col) + "^";
        }
    }

    static void runTests(String title, String[] cases) {
        System.out.println("\n-- " + title + " --");
        for (String c : cases) {
            System.out.printf("%-14s => %s%n", c, parse(c));
        }
    }

    public static void main(String[] args) {
        String[] accept = { "p", "p | q", "p & q", "!p", "p | q & r",
                            "!(p & q)", "(p | q) & r", "!p | q" };
        String[] reject = { "p |", "& q", "p & | q", "(p | q", "p | q)", "!", "(p &)" };

        System.out.println("Propositional Calculus Parser");
        trace = false;
        runTests("Should ACCEPT", accept);
        runTests("Should REJECT", reject);

        trace = true;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== Interactive mode (blank line to quit) ===");
        while (true) {
            System.out.print("Enter formula: ");
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine();
            if (line.trim().isEmpty()) break;
            System.out.println();
            System.out.println("\nResult: " + parse(line) + "\n");
        }
        sc.close();
    }
}
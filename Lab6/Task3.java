import java.util.*;


public class Task3 {

    static class Token {
        final String kind;   // "id", "&", "|", "!", "(", ")", "->", "<->", "$"
        final String text;
        final int col;
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
                out.add(new Token(String.valueOf(c), String.valueOf(c), i)); i++; continue;
            }
            if (s.startsWith("<->", i)) { out.add(new Token("<->", "<->", i)); i += 3; continue; }
            if (s.startsWith("->", i))  { out.add(new Token("->", "->", i));   i += 2; continue; }
            if (Character.isLetter(c)) {
                int start = i;
                while (i < s.length() && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_')) i++;
                out.add(new Token("id", s.substring(start, i), start));
                continue;
            }
            throw new SyntaxError("Lexical Error: invalid character '" + c + "'", i);
        }
        out.add(new Token("$", "end of input", s.length()));
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

    // B -> I B'
    static String B() {
        enter("B()");
        String result = BPrime(I());
        leave();
        return result;
    }

    // B' -> <-> I B' | ε
    static String BPrime(String left) {
        enter("BPrime()");
        String result = left;
        if (current().kind.equals("<->")) {
            match("<->");
            String right = I();
            result = BPrime("(" + left + " <-> " + right + ")");
        }
        leave();
        return result;
    }

    // I -> E I'
    static String I() {
        enter("I()");
        String result = IPrime(E());
        leave();
        return result;
    }

    // I' -> -> I | ε      (calls I again -> right associativity)
    static String IPrime(String left) {
        enter("IPrime()");
        String result = left;
        if (current().kind.equals("->")) {
            match("->");
            String right = I();
            result = "(" + left + " -> " + right + ")";
        }
        leave();
        return result;
    }

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
        leave();
        return result;
    }

    // F -> ! F | ( B ) | id
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
            result = B();
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
            String structure = B();
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
            System.out.printf("%-25s => %s%n", c, parse(c));
        }
    }

    public static void main(String[] args) {
        String[] accept = {
            "p -> q", "p & q -> r", "(p | q) -> r", "p <-> q",   // from the lab sheet
            "p -> q -> r",                                       // right associative
            "p <-> q -> r",                                      // -> binds tighter than <->
            "!p | q -> r & s",
            "(p -> q) <-> (!q -> !p)",
            "p | q & r"                                          // old Task 2 formulas still work
        };
        String[] reject = { "p ->", "-> q", "p <->", "p -> -> q", "(p -> q", "p <- q", "p <-> q)" };

        System.out.println("Extended Propositional Calculus Parser");
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
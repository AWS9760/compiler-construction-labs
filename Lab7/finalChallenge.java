package Lab7;

import java.util.*;

public class finalChallenge {

    static final String EPS = "#";
    static final String END = "$";

    static Map<String, List<String>> grammar = new LinkedHashMap<>();
    static Map<String, Set<String>> first = new LinkedHashMap<>();
    static Map<String, Set<String>> follow = new LinkedHashMap<>();
    static String start = null;

    // rows of the "set updates" table: {iteration, production, set, new symbols}
    static List<String[]> updates = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);

    // ================= Helpers =================

    static boolean isNonTerminal(char c) { return Character.isUpperCase(c); }

    static String readLine() {
        if (!sc.hasNextLine()) return null;
        return sc.nextLine();
    }

    // Formats a set as { a, b, # } with # and $ always shown last
    static String fmt(Collection<String> set) {
        List<String> items = new ArrayList<>();
        for (String s : set) if (!s.equals(EPS) && !s.equals(END)) items.add(s);
        if (set.contains(END)) items.add(END);
        if (set.contains(EPS)) items.add(EPS);
        return items.isEmpty() ? "{ }" : "{ " + String.join(", ", items) + " }";
    }

    static String production(String A) {
        return A + " -> " + String.join(" | ", grammar.get(A));
    }

    static void printTable(String[] headers, List<String[]> rows) {
        int[] w = new int[headers.length];
        for (int i = 0; i < headers.length; i++) w[i] = headers[i].length();
        for (String[] r : rows)
            for (int i = 0; i < r.length; i++) w[i] = Math.max(w[i], r[i].length());

        StringBuilder line = new StringBuilder("+");
        for (int x : w) line.append("-".repeat(x + 2)).append("+");

        System.out.println(line);
        System.out.println(row(headers, w));
        System.out.println(line);
        for (String[] r : rows) System.out.println(row(r, w));
        System.out.println(line);
    }

    static String row(String[] cells, int[] w) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++)
            sb.append(" ").append(String.format("%-" + w[i] + "s", cells[i])).append(" |");
        return sb.toString();
    }

    static boolean needGrammar() {
        if (grammar.isEmpty()) {
            System.out.println("No grammar entered yet. Choose option 1 first.");
            return false;
        }
        return true;
    }

    static boolean askYesNo(String question) {
        System.out.print(question + " (y/n): ");
        String ans = readLine();
        return ans != null && ans.trim().toLowerCase().startsWith("y");
    }

    // ================= Option 1: Enter / Replace Grammar =================

    /* Validates one production line and adds it to g.
       Returns null if OK, otherwise an error message. */
    static String parseProduction(String raw, Map<String, List<String>> g) {
        String line = raw.replaceAll("\\s+", "");

        int arrow = line.indexOf("->");
        if (arrow == -1)
            return "missing '->' (expected format: A->alpha|beta)";
        if (line.indexOf("->", arrow + 2) != -1)
            return "more than one '->' in the line";

        String lhs = line.substring(0, arrow);
        String rhs = line.substring(arrow + 2);

        if (lhs.isEmpty())
            return "left-hand side is empty";
        if (lhs.length() != 1 || !isNonTerminal(lhs.charAt(0)))
            return "left-hand side '" + lhs + "' must be a single uppercase letter";
        if (rhs.isEmpty())
            return "right-hand side is empty (write # for epsilon)";

        String[] alts = rhs.split("\\|", -1);
        for (String alt : alts) {
            if (alt.isEmpty())
                return "empty alternative found (use # for epsilon, e.g. A->aA|#)";
            if (alt.contains(EPS) && !alt.equals(EPS))
                return "'#' (epsilon) must be an alternative on its own, found '" + alt + "'";
            if (alt.contains(END))
                return "'$' is reserved for end of input and cannot appear in a production";
        }

        g.putIfAbsent(lhs, new ArrayList<>());
        for (String alt : alts)
            if (!g.get(lhs).contains(alt)) g.get(lhs).add(alt);   // no duplicate alternatives
        return null;
    }

    static void enterGrammar() {
        System.out.println("Enter one production per line, e.g.  E->TQ   or   Q->+TQ|#");
        System.out.println("Use # for epsilon. The first non-terminal is the start symbol.");
        System.out.println("Press Enter on an empty line to finish.");

        Map<String, List<String>> g = new LinkedHashMap<>();
        String newStart = null;
        int lineNo = 0;

        while (true) {
            System.out.print("  > ");
            String line = readLine();
            if (line == null || line.trim().isEmpty()) break;
            lineNo++;
            String err = parseProduction(line, g);
            if (err != null) {
                System.out.println("    Invalid production: " + err + ". Line ignored, please re-enter it.");
            } else if (newStart == null) {
                newStart = line.replaceAll("\\s+", "").substring(0, 1);
            }
        }

        if (g.isEmpty()) {
            System.out.println("No valid productions entered. "
                    + (grammar.isEmpty() ? "" : "Previous grammar kept."));
            return;
        }

        // every non-terminal used on a right-hand side must have a production
        Set<String> undefined = new LinkedHashSet<>();
        for (List<String> alts : g.values())
            for (String alt : alts)
                for (char c : alt.toCharArray())
                    if (isNonTerminal(c) && !g.containsKey(String.valueOf(c)))
                        undefined.add(String.valueOf(c));
        if (!undefined.isEmpty()) {
            System.out.println("Grammar rejected: non-terminal(s) " + undefined
                    + " are used but have no production."
                    + (grammar.isEmpty() ? "" : " Previous grammar kept."));
            return;
        }

        grammar = g;
        start = newStart;
        first.clear();
        follow.clear();
        System.out.println("Grammar stored: " + grammar.size()
                + " non-terminal(s), start symbol = " + start);
    }

    // ================= Option 2: Display Grammar =================

    static void displayGrammar() {
        Set<String> terminals = new LinkedHashSet<>();
        for (List<String> alts : grammar.values())
            for (String alt : alts)
                for (char c : alt.toCharArray())
                    if (!isNonTerminal(c) && c != '#') terminals.add(String.valueOf(c));

        System.out.println("\nGrammar (start symbol: " + start + ")");
        for (String A : grammar.keySet()) System.out.println("  " + production(A));
        System.out.println("Non-terminals: " + fmt(grammar.keySet()));
        System.out.println("Terminals    : " + fmt(terminals));
    }

    // ================= FIRST =================

    // FIRST of a string of grammar symbols (correct ε handling)
    static Set<String> firstOfSequence(String seq) {
        Set<String> result = new LinkedHashSet<>();
        if (seq.isEmpty() || seq.equals(EPS)) {
            result.add(EPS);
            return result;
        }
        for (char c : seq.toCharArray()) {
            String sym = String.valueOf(c);
            if (!isNonTerminal(c)) {          // terminal: it is the first symbol, stop
                result.add(sym);
                return result;
            }
            Set<String> f = new LinkedHashSet<>(first.get(sym));
            boolean nullable = f.remove(EPS);
            result.addAll(f);                 // add FIRST(sym) without ε
            if (!nullable) return result;     // sym is not nullable, stop
        }
        result.add(EPS);                      // every symbol was nullable
        return result;
    }

    static void computeFirst(boolean record) {
        first.clear();
        for (String A : grammar.keySet()) first.put(A, new LinkedHashSet<>());
        if (record) updates.clear();

        int iteration = 0;
        boolean changed;
        do {
            iteration++;
            changed = false;
            for (String A : grammar.keySet()) {
                Set<String> added = new LinkedHashSet<>();
                for (String rhs : grammar.get(A)) {
                    for (String s : firstOfSequence(rhs))
                        if (first.get(A).add(s)) added.add(s);
                }
                if (!added.isEmpty()) {
                    changed = true;
                    if (record)
                        updates.add(new String[]{ String.valueOf(iteration), production(A),
                                "FIRST(" + A + ")", String.join(", ", added) });
                }
            }
            if (record && !changed)
                updates.add(new String[]{ String.valueOf(iteration), "(all productions)",
                        "-", "no change -> fixed point reached" });
        } while (changed);
    }

    // ================= FOLLOW =================

    static void computeFollow(boolean record) {
        follow.clear();
        for (String A : grammar.keySet()) follow.put(A, new LinkedHashSet<>());
        follow.get(start).add(END);
        if (record) {
            updates.clear();
            updates.add(new String[]{ "0", "(start symbol rule)", "FOLLOW(" + start + ")", END });
        }

        int iteration = 0;
        boolean changed;
        do {
            iteration++;
            changed = false;
            for (String A : grammar.keySet()) {
                Map<String, Set<String>> addedFor = new LinkedHashMap<>();
                for (String rhs : grammar.get(A)) {
                    for (int i = 0; i < rhs.length(); i++) {
                        char c = rhs.charAt(i);
                        if (!isNonTerminal(c)) continue;
                        String B = String.valueOf(c);
                        String beta = rhs.substring(i + 1);

                        // rule: A -> αBβ  =>  FIRST(β) - {ε} goes into FOLLOW(B)
                        Set<String> firstBeta = firstOfSequence(beta);
                        Set<String> toAdd = new LinkedHashSet<>(firstBeta);
                        toAdd.remove(EPS);

                        // rule: β nullable or empty  =>  FOLLOW(A) goes into FOLLOW(B)
                        if (firstBeta.contains(EPS)) toAdd.addAll(follow.get(A));

                        for (String s : toAdd)
                            if (follow.get(B).add(s))
                                addedFor.computeIfAbsent(B, k -> new LinkedHashSet<>()).add(s);
                    }
                }
                if (!addedFor.isEmpty()) {
                    changed = true;
                    if (record)
                        for (Map.Entry<String, Set<String>> e : addedFor.entrySet())
                            updates.add(new String[]{ String.valueOf(iteration), production(A),
                                    "FOLLOW(" + e.getKey() + ")", String.join(", ", e.getValue()) });
                }
            }
            if (record && !changed)
                updates.add(new String[]{ String.valueOf(iteration), "(all productions)",
                        "-", "no change -> fixed point reached" });
        } while (changed);
    }

    static void showUpdates(String title) {
        System.out.println("\n" + title);
        printTable(new String[]{ "Iteration", "Production", "Set Updated", "New Symbol(s)" }, updates);
    }

    static void displaySets(String title, Map<String, Set<String>> sets) {
        System.out.println("\n" + title + "   (# = epsilon)");
        for (String A : sets.keySet())
            System.out.println("  " + title.split(" ")[0] + "(" + A + ") = " + fmt(sets.get(A)));
    }

    // ================= Option 5 / 6 =================

    static void displayTogether() {
        computeFirst(false);
        computeFollow(false);
        List<String[]> rows = new ArrayList<>();
        for (String A : grammar.keySet())
            rows.add(new String[]{ A, first.get(A).contains(EPS) ? "Yes" : "No",
                    fmt(first.get(A)), fmt(follow.get(A)) });
        System.out.println("\nFIRST and FOLLOW sets   (# = epsilon, start symbol: " + start + ")");
        printTable(new String[]{ "Non-terminal", "Nullable", "FIRST", "FOLLOW" }, rows);
    }

    static void checkNullable() {
        System.out.print("Enter non-terminal: ");
        String A = readLine();
        if (A == null) return;
        A = A.trim();
        if (!grammar.containsKey(A)) {
            System.out.println("'" + A + "' is not a non-terminal of this grammar. Non-terminals: "
                    + fmt(grammar.keySet()));
            return;
        }
        computeFirst(false);
        if (!first.get(A).contains(EPS)) {
            System.out.println(A + " is NOT nullable: every alternative of " + production(A)
                    + " contains a terminal or a non-nullable non-terminal.");
            return;
        }
        // find an alternative that shows why A is nullable
        for (String alt : grammar.get(A)) {
            if (alt.equals(EPS)) {
                System.out.println(A + " is nullable: it has the epsilon-production " + A + " -> #");
                return;
            }
        }
        for (String alt : grammar.get(A)) {
            if (firstOfSequence(alt).contains(EPS)) {
                System.out.println(A + " is nullable: in " + A + " -> " + alt
                        + " every symbol can derive epsilon.");
                return;
            }
        }
    }

    // ================= Main menu =================

    public static void main(String[] args) {
        System.out.println("=== FIRST / FOLLOW Grammar Analyzer ===");
        while (true) {
            System.out.println("\n1. Enter / Replace Grammar");
            System.out.println("2. Display Grammar");
            System.out.println("3. Compute FIRST Sets");
            System.out.println("4. Compute FOLLOW Sets");
            System.out.println("5. Display FIRST and FOLLOW Together");
            System.out.println("6. Check Whether a Non-terminal Is Nullable");
            System.out.println("7. Exit");
            System.out.print("Choice: ");

            String choice = readLine();
            if (choice == null) break;
            switch (choice.trim()) {
                case "1" -> enterGrammar();
                case "2" -> { if (needGrammar()) displayGrammar(); }
                case "3" -> {
                    if (!needGrammar()) break;
                    boolean steps = askYesNo("Show set updates for each iteration?");
                    computeFirst(steps);
                    if (steps) showUpdates("FIRST computation - set updates");
                    displaySets("FIRST Sets", first);
                }
                case "4" -> {
                    if (!needGrammar()) break;
                    boolean steps = askYesNo("Show set updates for each iteration?");
                    computeFirst(false);              // FOLLOW needs FIRST
                    computeFollow(steps);
                    if (steps) showUpdates("FOLLOW computation - set updates");
                    displaySets("FOLLOW Sets", follow);
                }
                case "5" -> { if (needGrammar()) displayTogether(); }
                case "6" -> { if (needGrammar()) checkNullable(); }
                case "7" -> {
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid choice. Enter a number from 1 to 7.");
            }
        }
    }
}
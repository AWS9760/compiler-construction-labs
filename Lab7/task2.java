package Lab7;

import java.util.*;

public class task2 {

    static Map<String, List<String>> grammar = new LinkedHashMap<>();
    static Map<String, Set<String>> first = new LinkedHashMap<>();
    static Map<String, Set<String>> follow = new LinkedHashMap<>();

    static boolean isNonTerminal(String s) {
        return s.length() == 1 && Character.isUpperCase(s.charAt(0));
    }

    // FIRST of a string of grammar symbols
    static Set<String> firstOfSequence(String seq) {
        Set<String> result = new LinkedHashSet<>();
        if (seq.equals("#")) {
            result.add("#");
            return result;
        }
        boolean allNullable = true;
        for (int i = 0; i < seq.length(); i++) {
            String symbol = String.valueOf(seq.charAt(i));
            if (!isNonTerminal(symbol)) {
                result.add(symbol);
                allNullable = false;
                break;
            }
            Set<String> symFirst = new LinkedHashSet<>(first.get(symbol));
            symFirst.remove("#");               // FIX: never copy ε here
            result.addAll(symFirst);
            if (!first.get(symbol).contains("#")) {
                allNullable = false;
                break;
            }
        }
        if (allNullable)
            result.add("#");
        return result;
    }

    static void computeFirst() {
        for (String nt : grammar.keySet())
            first.put(nt, new LinkedHashSet<>());
        boolean changed;
        do {
            changed = false;
            for (String nt : grammar.keySet()) {
                for (String rhs : grammar.get(nt)) {
                    Set<String> temp = firstOfSequence(rhs);
                    if (first.get(nt).addAll(temp))
                        changed = true;
                }
            }
        } while (changed);
    }

    static void computeFollow(String start) {
        for (String nt : grammar.keySet())
            follow.put(nt, new LinkedHashSet<>());
        follow.get(start).add("$");
        boolean changed;
        do {
            changed = false;
            for (String A : grammar.keySet()) {
                for (String rhs : grammar.get(A)) {
                    for (int i = 0; i < rhs.length(); i++) {
                        String B = String.valueOf(rhs.charAt(i));
                        if (!isNonTerminal(B))
                            continue;
                        String beta = rhs.substring(i + 1);
                        Set<String> firstBeta = firstOfSequence(beta.isEmpty() ? "#" : beta);
                        Set<String> toAdd = new LinkedHashSet<>(firstBeta);
                        toAdd.remove("#");
                        if (follow.get(B).addAll(toAdd))
                            changed = true;
                        if (beta.isEmpty() || firstBeta.contains("#")) {
                            if (follow.get(B).addAll(follow.get(A)))
                                changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    static void displaySets(String title, Map<String, Set<String>> sets) {
        System.out.println("\n" + title);
        for (String nt : sets.keySet()) {
            System.out.println(nt + " = " + sets.get(nt));
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Number of productions: ");
        int n = Integer.parseInt(sc.nextLine());
        String start = "";
        System.out.println("Enter productions such as E->TQ or Q->+TQ|#");
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine().replace(" ", "");
            String[] parts = input.split("->");
            String lhs = parts[0];
            String[] alternatives = parts[1].split("\\|");
            if (i == 0)
                start = lhs;
            grammar.putIfAbsent(lhs, new ArrayList<>());
            for (String rhs : alternatives)
                grammar.get(lhs).add(rhs);
        }
        computeFirst();
        computeFollow(start);
        displaySets("FIRST Sets", first);
        displaySets("FOLLOW Sets", follow);
        sc.close();
    }
}
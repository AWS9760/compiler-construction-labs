package Lab4;

import java.util.HashMap;
import java.util.regex.*;

public class lab4q1 {

    static String keywordPattern = "\\b(if|else|while|for|return)\\b";
    static String dataTypePattern = "\\b(int|float|double|char|boolean|String)\\b";
    static String numberPattern = "\\b\\d+(\\.\\d+)?\\b";
    static String romanPattern = "\\b(XV|XX|III|IV|II|I|V|X|L|C|D|M)\\b";
    static String identifierPattern = "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b";
    static String operatorPattern = "(\\+\\+|--|==|<=|>=|!=|[=+\\-*/<>])";
    static String delimiterPattern = "[;(){}]";

    static String combinedRegex = keywordPattern + "|" + dataTypePattern + "|" + numberPattern + "|" +
     romanPattern + "|" + identifierPattern + "|" + operatorPattern + "|" + delimiterPattern;

    static HashMap<String, String[]> symbolTable;
    static int idCounter;

    static boolean insideForParens;
    static boolean expectForParen;
    static int parenDepth;
    static String pendingDataType;
    public static void main(String[] args) {
        tokenize("int x = 5; for (int i = 0; i < 10; i++) { }");
    }

    static void tokenize(String input){
        symbolTable = new HashMap<>();
        idCounter = 1;

        insideForParens = false;
        expectForParen = false;
        parenDepth = 0;
        pendingDataType = null;

        System.out.println("Input: " + input);
        System.out.println("Token Stream:");

        Pattern p = Pattern.compile(combinedRegex);
        Matcher m = p.matcher(input);
        int lastEnd = 0;

        while (m.find()) {
            if(m.start() > lastEnd)
                reportErrors(input.substring(lastEnd, m.start()));

            lastEnd = m.end();
            String token = m.group();
            System.out.println(classifyToken(token));

            handleSymbolTable(token);
        }

        if(lastEnd < input.length())
            reportErrors(input.substring(lastEnd));

        System.out.println("\nSymbol Table:");
        System.out.printf("%-10s %-8s %-10s %-8s%n", "Name", "ID", "DataType", "Scope");

        for(String key : symbolTable.keySet()){
            String[] info = symbolTable.get(key);
            System.out.printf("%-10s %-8s %-10s %-8s%n", key, info[0], info[1], info[2]);
        }
        System.out.println();
    }

    static void handleSymbolTable(String token){
        if(token.equals("for")){
            expectForParen = true;
        }
        else if (token.equals("(")) {
            if(expectForParen){
                insideForParens = true;
                expectForParen = false;
                parenDepth = 1;
            }
            else if (insideForParens) {
                parenDepth++;
            }            
        }
        else if (token.equals(")")) {
            if(insideForParens){
                parenDepth--;
                
                if (parenDepth == 0) insideForParens = false;
            }
        }

        if(token.matches(dataTypePattern)){
            pendingDataType = token;
            return;
        }

        boolean isPlainIdentifier = token.matches(identifierPattern) && !token.matches(keywordPattern) &&
         !token.matches(dataTypePattern) && !token.matches(romanPattern);
        
        if(isPlainIdentifier){
            String scope = insideForParens ? "local" : "global";
            String dataType = (pendingDataType != null) ? pendingDataType : "-";

            if(!symbolTable.containsKey(token)){
                symbolTable.put(token, new String[]{"ID_" + idCounter++, dataType, scope});
            }

            pendingDataType = null;
        }
        else if(!token.matches(operatorPattern) && !token.matches(delimiterPattern) && !token.matches(numberPattern)){
            pendingDataType = null;
        }
    }

    static void reportErrors(String gap){
        for(char c : gap.toCharArray())
            if(!Character.isWhitespace(c))
                System.out.println("LEXICAL ERROR: " + c);
    }

    static String classifyToken(String token){
        if(token.matches(keywordPattern)) return "KEYWORD(" + token + ")";
        if(token.matches(dataTypePattern)) return "DATATYPE(" + token + ")";
        if(token.matches(numberPattern)) return "NUMBER(" + token + ")";
        if(token.matches(romanPattern)) return "ROMAN(" + token + ")";
        if(token.matches(identifierPattern)) return "IDENTIFIER(" + token + ")";
        if(token.matches(operatorPattern)) return "OPERATOR(" + token + ")";
        if(token.matches(delimiterPattern)) return "DELIMITER(" + token + ")";
        return "UNKNOWN(" + token + ")";
    }
}
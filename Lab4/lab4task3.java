package Lab4;

import java.util.HashMap;
import java.util.regex.*;

public class lab4task3 {

    static String keywordPattern = "\\b(for)\\b";
    static String dataTypePattern = "\\b(int|float|double|char|boolean|String)\\b";
    static String numberPattern = "\\b\\d+(\\.\\d+)?\\b";
    static String romanPattern = "\\b(XV|XX|III|IV|II|I|V|X|L|C|D|M)\\b";
    static String identifierPattern = "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b";
    static String operatorPattern = "(\\+\\+|--|==|<=|>=|!=|[=+\\-*/<>])";
    static String delimiterPattern = "[;(){}]";

    static String combinedRegex = keywordPattern + "|" + dataTypePattern + "|" + numberPattern + "|" +
     romanPattern + "|" + identifierPattern + "|" + operatorPattern + "|" + delimiterPattern;

    static HashMap<String, String> symbolTable;
    static int idCounter;
    public static void main(String[] args) {
        tokenize("int x = X;");
        tokenize("for (int i = 1; i < 10; i++)");
    }

    static void tokenize(String input){
        symbolTable = new HashMap<>();
        idCounter = 1;

        System.out.println("Input: " + input);
        System.out.println("Token Stream:");

        Pattern p = Pattern.compile(combinedRegex);
        Matcher m = p.matcher(input);

        while (m.find()) {
            String token = m.group();
            System.out.println(classifyToken(m.group()));

            boolean isPlainIdentifier = token.matches(identifierPattern) && !token.matches(keywordPattern) &&
             !token.matches(dataTypePattern) && !token.matches(romanPattern);

            if(isPlainIdentifier)
                symbolTable.putIfAbsent(token, "ID_" + idCounter++);
        }

        System.out.println("Symbol Table:");
        for(String key : symbolTable.keySet())
            System.out.println(key + " : " + symbolTable.get(key));
        System.out.println();
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
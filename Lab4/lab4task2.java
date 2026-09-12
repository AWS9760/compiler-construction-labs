package Lab4;

import java.util.HashMap;
import java.util.regex.*;

public class lab4task2 {

    static String keywordPattern = "\\b(for)\\b";
    static String dataTypePattern = "\\b(int|float|double|char|boolean|String)\\b";
    static String numberPattern = "\\b\\d+(\\.\\d+)?\\b";
    static String romanPattern = "\\b(XV|XX|III|IV|II|I|V|X|L|C|D|M)\\b";
    static String identifierPattern = "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b";
    static String operatorPattern = "(\\+\\+|--|==|<=|>=|!=|[=+\\-*/<>])";
    static String delimiterPattern = "[;(){}]";

    static String combinedRegex = keywordPattern + "|" + dataTypePattern + "|" + numberPattern + "|" +
     romanPattern + "|" + identifierPattern + "|" +operatorPattern + "|" + delimiterPattern;

    static HashMap<String, String> symbolTable = new HashMap<>();
    static int idCounter = 1;
    public static void main(String[] args) {
        String input = "for (int i = 1; i < 10; i++)";
        System.out.println("Input: " + input);
        System.out.println("Token Stream:");

        Pattern p = Pattern.compile(combinedRegex);
        Matcher m = p.matcher(input);

        while (m.find()) {
            String token = m.group();
            System.out.println(classifyToken(m.group()));

            if(token.matches(identifierPattern) && !token.matches(keywordPattern) && !token.matches(dataTypePattern))
                symbolTable.putIfAbsent(token, "ID_" + idCounter++);
        }

        System.out.println("\nSymbol Table:");
        for(String key : symbolTable.keySet())
            System.out.println(key + " : " + symbolTable.get(key));
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
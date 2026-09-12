package Lab4;

import java.util.regex.*;

public class lab4task1 {

    static String dataTypePattern = "\\b(int|float|double|char|boolean|String)\\b";
    static String romanPattern = "\\b(XV|XX|III|IV|II|I|V|X|L|C|D|M)\\b";
    static String identifierPattern = "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b";
    static String operatorPattern = "[=+\\-*/]";
    static String delimiterPattern = "[;(){}]";

    static String combinedRegex = dataTypePattern + "|" + romanPattern + "|" + identifierPattern + "|" +
     operatorPattern + "|" + delimiterPattern;

    public static void main(String[] args) {
        String input = "int x = X;";

        System.out.println("Input: " + input);
        System.out.println("Token Stream:");

        Pattern p = Pattern.compile(combinedRegex);
        Matcher m = p.matcher(input);

        while (m.find()) {
            System.out.println(classifyToken(m.group()));
        }
    }

    static String classifyToken(String token){
        if(token.matches(dataTypePattern)) return "DATATYPE(" + token + ")";
        if(token.matches(romanPattern)) return "ROMAN(" + token + ")";
        if(token.matches(identifierPattern)) return "IDENTIFIER(" + token + ")";
        if(token.matches(operatorPattern)) return "OPERATOR(" + token + ")";
        if(token.matches(delimiterPattern)) return "DELIMITER(" + token + ")";
        return "UNKNOWN(" + token + ")";
    }
}
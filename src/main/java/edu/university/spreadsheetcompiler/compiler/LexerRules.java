package edu.university.spreadsheetcompiler.compiler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class LexerRules {
    private LexerRules() {}

    // Ordered rules used by the lexer (first match wins).
    public static final Map<TokenType, Pattern> TOKEN_REGEX = orderedRules();

    // Primitive regex pieces
    public static final String WS = "\\s+";
    public static final String NUMBER = "\\d+";
    public static final String CELL_REF = "[A-Z]+[1-9]\\d*";
    public static final String IDENTIFIER = "[A-Z]+";

    // Supported functions
    public static final String FUNCTION_NAMES = "SUM|MAX|MIN|IF";

    private static Map<TokenType, Pattern> orderedRules() {
        Map<TokenType, Pattern> rules = new LinkedHashMap<>();
        rules.put(TokenType.GTE, Pattern.compile(">="));
        rules.put(TokenType.LTE, Pattern.compile("<="));
        rules.put(TokenType.EQUALS, Pattern.compile("="));
        rules.put(TokenType.GT, Pattern.compile(">"));
        rules.put(TokenType.LT, Pattern.compile("<"));
        rules.put(TokenType.PLUS, Pattern.compile("\\+"));
        rules.put(TokenType.MINUS, Pattern.compile("-"));
        rules.put(TokenType.STAR, Pattern.compile("\\*"));
        rules.put(TokenType.SLASH, Pattern.compile("/"));
        rules.put(TokenType.LPAREN, Pattern.compile("\\("));
        rules.put(TokenType.RPAREN, Pattern.compile("\\)"));
        rules.put(TokenType.COMMA, Pattern.compile(","));
        rules.put(TokenType.COLON, Pattern.compile(":"));
        rules.put(TokenType.NUMBER, Pattern.compile(NUMBER));
        rules.put(TokenType.CELL_REF, Pattern.compile(CELL_REF));
        rules.put(TokenType.FUNCTION, Pattern.compile(FUNCTION_NAMES));
        rules.put(TokenType.IDENTIFIER, Pattern.compile(IDENTIFIER));
        return rules;
    }
}

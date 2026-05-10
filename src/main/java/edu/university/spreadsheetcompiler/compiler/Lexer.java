package edu.university.spreadsheetcompiler.compiler;

import edu.university.spreadsheetcompiler.compiler.CompilerModels.CompilerError;
import edu.university.spreadsheetcompiler.compiler.CompilerModels.ParseResult;
import edu.university.spreadsheetcompiler.compiler.CompilerModels.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final String src;
    private final List<Token> tokens = new ArrayList<>();
    private final List<CompilerError> errors = new ArrayList<>();
    private int i = 0;

    public Lexer(String src) { this.src = src == null ? "" : src; }

    public ParseResult lex() {
        while (i < src.length()) {
            if (skipWhitespace()) {
                continue;
            }
            if (!matchTokenAtCurrentPosition()) {
                reportInvalidToken();
                i++;
            }
        }

        enforceLeadingEqualsRule();
        tokens.add(new Token(TokenType.EOF, "", src.length()));
        return new ParseResult(tokens, null, errors);
    }

    private boolean skipWhitespace() {
        Pattern ws = Pattern.compile(LexerRules.WS);
        Matcher matcher = ws.matcher(src);
        matcher.region(i, src.length());
        if (matcher.lookingAt()) {
            i = matcher.end();
            return true;
        }
        return false;
    }

    private boolean matchTokenAtCurrentPosition() {
        if (Character.isLetter(src.charAt(i))) {
            return matchAlphaNumericWord();
        }
        for (Map.Entry<TokenType, Pattern> entry : LexerRules.TOKEN_REGEX.entrySet()) {
            if (entry.getKey() == TokenType.CELL_REF || entry.getKey() == TokenType.FUNCTION || entry.getKey() == TokenType.IDENTIFIER) {
                continue;
            }
            Matcher matcher = entry.getValue().matcher(src);
            matcher.region(i, src.length());
            if (!matcher.lookingAt()) {
                continue;
            }

            String lexeme = matcher.group();
            tokens.add(new Token(entry.getKey(), lexeme, i));
            i = matcher.end();
            return true;
        }
        return false;
    }

    private boolean matchAlphaNumericWord() {
        int start = i;
        while (i < src.length() && Character.isLetterOrDigit(src.charAt(i))) {
            i++;
        }

        String raw = src.substring(start, i);
        String word = raw.toUpperCase();

        if (word.matches(LexerRules.CELL_REF)) {
            tokens.add(new Token(TokenType.CELL_REF, word, start));
            return true;
        }
        if (word.matches(LexerRules.FUNCTION_NAMES)) {
            tokens.add(new Token(TokenType.FUNCTION, word, start));
            return true;
        }
        if (word.matches("[A-Z]+\\d+")) {
            errors.add(new CompilerError(
                    "LEXER_INVALID_CELL_REFERENCE",
                    "Invalid cell reference '" + raw + "'",
                    1,
                    start + 1,
                    "Cell references must look like A1, B2, AA10 and row must be >= 1."
            ));
            return true;
        }
        errors.add(new CompilerError(
                "LEXER_UNSUPPORTED_IDENTIFIER",
                "Unsupported identifier '" + raw + "'. Allowed functions: SUM, MAX, MIN, IF",
                1,
                start + 1,
                "Use a cell reference like A1 or a supported function name."
        ));
        return true;
    }

    private void reportInvalidToken() {
        char c = src.charAt(i);
        errors.add(new CompilerError(
                "LEXER_INVALID_TOKEN",
                "Invalid token '" + c + "'",
                1,
                i + 1,
                "Remove or replace the invalid character."
        ));
    }

    private void enforceLeadingEqualsRule() {
        int firstNonSpace = firstNonSpaceIndex();
        if (firstNonSpace == -1) {
            return;
        }
        Token firstRealToken = tokens.isEmpty() ? null : tokens.get(0);
        if (firstRealToken == null || firstRealToken.type() != TokenType.EQUALS) {
            errors.add(new CompilerError(
                    "LEXER_MISSING_EQUALS_PREFIX",
                    "Formula must start with '='",
                    1,
                    firstNonSpace + 1,
                    "Start spreadsheet formulas with '=' (example: =SUM(A1:A5))."
            ));
        }
    }

    private int firstNonSpaceIndex() {
        for (int idx = 0; idx < src.length(); idx++) {
            if (!Character.isWhitespace(src.charAt(idx))) {
                return idx;
            }
        }
        return -1;
    }
}

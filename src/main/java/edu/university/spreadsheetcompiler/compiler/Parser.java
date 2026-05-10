package edu.university.spreadsheetcompiler.compiler;

import edu.university.spreadsheetcompiler.compiler.CompilerModels.CompilerError;
import edu.university.spreadsheetcompiler.compiler.CompilerModels.Token;
import edu.university.spreadsheetcompiler.compiler.ast.AstNode;
import edu.university.spreadsheetcompiler.compiler.ast.BinaryExpression;
import edu.university.spreadsheetcompiler.compiler.ast.CellNode;
import edu.university.spreadsheetcompiler.compiler.ast.FunctionCallNode;
import edu.university.spreadsheetcompiler.compiler.ast.NumberNode;
import edu.university.spreadsheetcompiler.compiler.ast.RangeNode;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final List<CompilerError> errors = new ArrayList<>();
    private final boolean validateFunctions;
    private int current = 0;

    public Parser(List<Token> tokens, boolean validateFunctions) {
        this.tokens = tokens;
        this.validateFunctions = validateFunctions;
    }

    public AstNode parse() {
        match(TokenType.EQUALS);
        AstNode expr = expression();
        if (!isAtEnd()) {
            error(peek(), "PARSER_UNEXPECTED_TOKEN", "Unexpected token after expression");
        }
        return expr;
    }

    public List<CompilerError> getErrors() { return errors; }

    // expression -> comparison
    private AstNode expression() { return comparison(); }

    // comparison -> term ( ( ">" | "<" | ">=" | "<=" ) term )*
    private AstNode comparison() {
        AstNode expr = term();
        while (match(TokenType.GT, TokenType.LT, TokenType.GTE, TokenType.LTE)) {
            Token op = previous();
            AstNode right = term();
            expr = new BinaryExpression(op.lexeme(), expr, right);
        }
        return expr;
    }

    // term -> factor ( ( "+" | "-" ) factor )*
    private AstNode term() {
        AstNode expr = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token op = previous();
            expr = new BinaryExpression(op.lexeme(), expr, factor());
        }
        return expr;
    }

    // factor -> unary ( ( "*" | "/" ) unary )*
    private AstNode factor() {
        AstNode expr = unary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token op = previous();
            expr = new BinaryExpression(op.lexeme(), expr, unary());
        }
        return expr;
    }
    private AstNode unary() {
        if (match(TokenType.MINUS)) {
            return new BinaryExpression("*", new NumberNode(-1), primary());
        }
        return primary();
    }
    // unary -> "-" unary | primary
    // primary -> NUMBER | CELL_REF range? | function_call | "(" expression ")"
    private AstNode primary() {
        if (match(TokenType.NUMBER)) return new NumberNode(Double.parseDouble(previous().lexeme()));
        if (match(TokenType.CELL_REF)) {
            Token start = previous();
            if (match(TokenType.COLON)) {
                Token end = consume(TokenType.CELL_REF, "Expected cell reference after ':'");
                return new RangeNode(new CellNode(start.lexeme()), new CellNode(end.lexeme()));
            }
            return new CellNode(start.lexeme());
        }
        if (match(TokenType.FUNCTION, TokenType.IDENTIFIER)) {
            Token fn = previous();
            if (!check(TokenType.LPAREN)) {
                error(peek(), "PARSER_UNEXPECTED_TOKEN", "Expected '(' after function name");
                return new CellNode(fn.lexeme());
            }
            return functionCall(fn);
        }
        if (match(TokenType.LPAREN)) {
            AstNode expr = expression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return expr;
        }
        error(peek(), "PARSER_EXPECTED_EXPRESSION", "Expected expression");
        return new NumberNode(0);
    }

    // function_call -> FUNCTION "(" arguments? ")"
    // arguments -> expression ( "," expression )*
    private AstNode functionCall(Token fn) {
        consume(TokenType.LPAREN, "Expected '(' after function name");
        List<AstNode> args = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            args.add(expression());
            while (!check(TokenType.RPAREN) && !isAtEnd()) {
                if (match(TokenType.COMMA)) {
                    if (check(TokenType.RPAREN)) {
                        error(peek(), "PARSER_EXPECTED_EXPRESSION", "Expected expression after ','");
                        break;
                    }
                    args.add(expression());
                    continue;
                }

                // Strong missing-comma detection between two expressions.
                if (startsExpression(peek())) {
                    error(peek(), "PARSER_MISSING_COMMA", "Expected ',' between function arguments");
                    args.add(expression());
                    continue;
                }

                error(peek(), "PARSER_UNEXPECTED_TOKEN", "Expected ',' or ')' after function argument");
                advance();
            }
        }
        consume(TokenType.RPAREN, "Expected ')' after function arguments");
        if (validateFunctions) validateFunction(fn, args.size());
        return new FunctionCallNode(fn.lexeme(), args);
    }

    private void validateFunction(Token fn, int argc) {
        String name = fn.lexeme().toUpperCase();
        if (name.equals("SUM") && argc < 1) error(fn, "PARSER_FUNCTION_ARITY", "SUM requires at least 1 argument");
        if (name.equals("IF") && argc != 3) error(fn, "PARSER_FUNCTION_ARITY", "IF requires exactly 3 arguments");
    }

    private Token consume(TokenType t, String msg) {
        if (check(t)) return advance();
        error(peek(), "PARSER_UNEXPECTED_TOKEN", msg);
        return peek();
    }
    private void error(Token token, String code, String msg) {
        errors.add(new CompilerError(code, msg, 1, token.position() + 1, "Check formula syntax near '" + token.lexeme() + "'"));
    }
    private boolean match(TokenType... types) {
        for (TokenType t : types) if (check(t)) { advance(); return true; }
        return false;
    }
    private boolean startsExpression(Token token) {
        return switch (token.type()) {
            case NUMBER, CELL_REF, FUNCTION, IDENTIFIER, LPAREN, MINUS -> true;
            default -> false;
        };
    }
    private boolean check(TokenType t) { return !isAtEnd() && peek().type() == t; }
    private Token advance() { if (!isAtEnd()) current++; return previous(); }
    private boolean isAtEnd() { return peek().type() == TokenType.EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }
}

package edu.university.spreadsheetcompiler.compiler;

import edu.university.spreadsheetcompiler.compiler.ast.AstNode;
import java.util.List;

public class CompilerModels {
    public record Token(TokenType type, String lexeme, int position) {}
    public record CompilerError(String code, String message, int line, int column, String hint) {}
    public record ParseOptions(boolean includeTokens, boolean includeAstJson, boolean includeAstTree, boolean validateFunctions) {
        public static ParseOptions defaults() { return new ParseOptions(true, true, true, true); }
    }
    public record ParseRequest(String formula, ParseOptions options) {}
    public record ParseResponse(String status, List<Token> tokens, Object ast, String astTree, List<CompilerError> errors) {}
    public record SimpleParseRequest(String formula) {}
    public record SimpleParseResponse(List<Token> tokens, Object ast, List<CompilerError> errors) {}
    public record SimpleParseWithResultResponse(List<Token> tokens, Object ast, Double finalResult, List<CompilerError> errors) {}
    public record ParseResult(List<Token> tokens, AstNode ast, List<CompilerError> errors) {}
}
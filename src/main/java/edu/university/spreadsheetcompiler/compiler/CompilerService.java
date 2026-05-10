package edu.university.spreadsheetcompiler.compiler;

import edu.university.spreadsheetcompiler.compiler.CompilerModels.ParseOptions;
import edu.university.spreadsheetcompiler.compiler.CompilerModels.ParseResult;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class CompilerService {

    public ParseResult parse(String formula, ParseOptions options) {
        ParseOptions effective = options == null ? ParseOptions.defaults() : options;
        String normalized = normalize(formula);
        ParseResult lexResult = new Lexer(normalized).lex();
        Parser parser = new Parser(lexResult.tokens(), effective.validateFunctions());
        var ast = parser.parse();
        var errors = new ArrayList<>(lexResult.errors());
        errors.addAll(parser.getErrors());
        return new ParseResult(lexResult.tokens(), ast, errors);
    }

    private String normalize(String formula) {
        if (formula == null) return "";
        return formula.trim();
    }
}

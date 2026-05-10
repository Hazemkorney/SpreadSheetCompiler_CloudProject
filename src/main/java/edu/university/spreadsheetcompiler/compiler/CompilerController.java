package edu.university.spreadsheetcompiler.compiler;

import edu.university.spreadsheetcompiler.compiler.CompilerModels.ParseRequest;
import edu.university.spreadsheetcompiler.compiler.CompilerModels.ParseResponse;
import edu.university.spreadsheetcompiler.compiler.ast.AstRenderer;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compiler")
public class CompilerController {
    private final CompilerService compilerService;

    public CompilerController(CompilerService compilerService) {
        this.compilerService = compilerService;
    }

    @PostMapping("/parse")
    public ParseResponse parse(@Valid @RequestBody ParseRequest request) {
        var options = request.options() == null ? CompilerModels.ParseOptions.defaults() : request.options();
        var result = compilerService.parse(request.formula(), options);
        Object astJson = options.includeAstJson() && result.ast() != null ? result.ast().toMap() : null;
        String astTree = options.includeAstTree() && result.ast() != null ? AstRenderer.toTree(result.ast()) : null;
        return new ParseResponse(
                result.errors().isEmpty() ? "SUCCESS" : "ERROR",
                options.includeTokens() ? result.tokens() : List.of(),
                astJson,
                astTree,
                result.errors()
        );
    }
}
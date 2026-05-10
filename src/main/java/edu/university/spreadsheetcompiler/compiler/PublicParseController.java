package edu.university.spreadsheetcompiler.compiler;

import edu.university.spreadsheetcompiler.compiler.CompilerModels.SimpleParseRequest;
import edu.university.spreadsheetcompiler.compiler.CompilerModels.SimpleParseResponse;
import edu.university.spreadsheetcompiler.compiler.CompilerModels.SimpleParseWithResultResponse;
import edu.university.spreadsheetcompiler.submission.SubmissionEntity;
import edu.university.spreadsheetcompiler.submission.SubmissionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicParseController {

    private final CompilerService compilerService;
    private final FormulaEvaluation formulaEvaluator;

    private final SubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;

    public PublicParseController(
            CompilerService compilerService,
            FormulaEvaluation formulaEvaluator,
            SubmissionRepository submissionRepository,
            ObjectMapper objectMapper
    ) {
        this.compilerService = compilerService;
        this.formulaEvaluator = formulaEvaluator;
        this.submissionRepository = submissionRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/parse")
    public SimpleParseResponse parse(@Valid @RequestBody SimpleParseRequest request) {
        var result = compilerService.parse(request.formula(), CompilerModels.ParseOptions.defaults());
        Object ast = result.ast() == null ? null : result.ast().toMap();

        return new SimpleParseResponse(
                result.tokens(),
                ast,
                result.errors()
        );
    }

    // ================= PARSE + RESULT + SAVE =================
    @PostMapping("/parse-with-result")
public SimpleParseWithResultResponse parseWithResult(
        @Valid @RequestBody SimpleParseRequest request,
        Principal principal) {

        var result = compilerService.parse(request.formula(), CompilerModels.ParseOptions.defaults());
        Object ast = result.ast() == null ? null : result.ast().toMap();

        // ================= EVALUATION =================
        Double finalResult = null;

        if (result.errors().isEmpty() && result.ast() != null) {
            try {
                finalResult = formulaEvaluator.evaluate(result.ast());
            } catch (IllegalArgumentException ignored) {
                finalResult = null;
            }
        }

        try {
            SubmissionEntity entity = new SubmissionEntity();
            entity.setUsername(principal.getName());
            entity.setFormula(request.formula());
            entity.setStatus(result.errors().isEmpty() ? "SUCCESS" : "ERROR");

            // ================= التعديل الجديد: حفظ النتيجة =================
            entity.setResult(finalResult); 
            // ==========================================================

            entity.setTokensJson(
                    objectMapper.writeValueAsString(result.tokens())
            );

            entity.setAstJson(
                    objectMapper.writeValueAsString(ast)
            );

            entity.setErrorsJson(
                    objectMapper.writeValueAsString(result.errors())
            );

            submissionRepository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save submission history", e);
        }

        return new SimpleParseWithResultResponse(
                result.tokens(),
                ast,
                finalResult,
                result.errors()
        );
    }
}
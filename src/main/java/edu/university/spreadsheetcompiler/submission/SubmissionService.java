package edu.university.spreadsheetcompiler.submission;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.university.spreadsheetcompiler.common.ApiException;
import edu.university.spreadsheetcompiler.compiler.CompilerModels;
import edu.university.spreadsheetcompiler.compiler.CompilerService;
import edu.university.spreadsheetcompiler.submission.SubmissionDtos.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CompilerService compilerService;
    private final ObjectMapper objectMapper;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            CompilerService compilerService,
            ObjectMapper objectMapper
    ) {
        this.submissionRepository = submissionRepository;
        this.compilerService = compilerService;
        this.objectMapper = objectMapper;
    }

    // ================= CREATE =================
    public CreateSubmissionResponse create(String username, CreateSubmissionRequest request) {

        var result = compilerService.parse(
                request.formula(),
                request.parseOptions()
        );

        SubmissionEntity entity = new SubmissionEntity();

        entity.setUsername(username);
        entity.setFormula(request.formula());
        entity.setStatus(result.errors().isEmpty() ? "SUCCESS" : "ERROR");

        try {
            entity.setTokensJson(objectMapper.writeValueAsString(result.tokens()));
            entity.setAstJson(objectMapper.writeValueAsString(
                    result.ast() == null ? null : result.ast().toMap()
            ));
            entity.setErrorsJson(objectMapper.writeValueAsString(result.errors()));

        } catch (Exception e) {
            throw new ApiException("Failed to serialize submission snapshot");
        }

        submissionRepository.save(entity);

        return new CreateSubmissionResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    // ================= SUBMIT =================
    public SubmitResponse submit(String username, SubmitRequest request) {

        var result = compilerService.parse(
                request.formula(),
                CompilerModels.ParseOptions.defaults()
        );

        SubmissionEntity entity = new SubmissionEntity();

        entity.setUsername(username);
        entity.setFormula(request.formula());
        entity.setStatus(result.errors().isEmpty() ? "SUCCESS" : "ERROR");

        try {
            entity.setTokensJson(objectMapper.writeValueAsString(result.tokens()));
            entity.setAstJson(objectMapper.writeValueAsString(
                    result.ast() == null ? null : result.ast().toMap()
            ));
            entity.setErrorsJson(objectMapper.writeValueAsString(result.errors()));

        } catch (Exception e) {
            throw new ApiException("Failed to serialize submission snapshot");
        }

        submissionRepository.save(entity);

        return new SubmitResponse(
                entity.getId(),
                entity.getFormula(),
                result.tokens(),
                result.ast() == null ? null : result.ast().toMap(),
                entity.getCreatedAt()
        );
    }

    // ================= HISTORY (USER ONLY) =================
    public HistoryListResponse list(String username, int page, int size) {

        var data = submissionRepository.findByUsernameOrderByCreatedAtDesc(
                username,
                PageRequest.of(page, size)
        );

        List<HistoryItem> items = data.getContent().stream()
                .map(s -> new HistoryItem(
                        s.getId(),
                        s.getFormula(),
                        s.getStatus(),
                        s.getCreatedAt()
                ))
                .toList();

        return new HistoryListResponse(
                items,
                page,
                size,
                data.getTotalElements()
        );
    }

    // ================= DETAIL =================
    public HistoryDetailResponse detail(String username, Long id) {

        SubmissionEntity s = submissionRepository
                .findByIdAndUsername(id, username)
                .orElseThrow(() -> new ApiException("Submission not found"));

        try {
            List<CompilerModels.Token> tokens =
                    objectMapper.readValue(s.getTokensJson(), new TypeReference<>() {});

            Object ast =
                    objectMapper.readValue(s.getAstJson(), Object.class);

            List<Map<String, Object>> errors =
                    objectMapper.readValue(s.getErrorsJson(), new TypeReference<>() {});

            return new HistoryDetailResponse(
                    s.getId(),
                    s.getFormula(),
                    tokens,
                    ast,
                    errors,
                    s.getStatus(),
                    s.getCreatedAt()
            );

        } catch (Exception e) {
            throw new ApiException("Failed to parse saved snapshot");
        }
    }
}
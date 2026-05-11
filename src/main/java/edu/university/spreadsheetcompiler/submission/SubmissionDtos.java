package edu.university.spreadsheetcompiler.submission;

import edu.university.spreadsheetcompiler.compiler.CompilerModels;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class SubmissionDtos {
    public record SubmitRequest(String formula) {}
    public record SubmitResponse(Long submissionId, String formula, List<CompilerModels.Token> tokens, Object ast, Instant timestamp) {}
    public record HistoryEntry(Long submissionId, String formula, List<CompilerModels.Token> tokens, Object ast, Instant timestamp) {}
    public record HistoryResponse(List<HistoryEntry> items) {}

    public record CreateSubmissionRequest(String formula, CompilerModels.ParseOptions parseOptions) {}
    public record CreateSubmissionResponse(Long submissionId, String status, Instant createdAt) {}
    public record HistoryItem(Long submissionId, String formula, String status, Instant createdAt) {}
    public record HistoryListResponse(List<HistoryItem> items, int page, int size, long totalElements) {}
    public record HistoryDetailResponse(Long submissionId, String formula, List<CompilerModels.Token> tokens, Object ast, List<Map<String, Object>> errors,
                                        String status, Instant createdAt) {}
}

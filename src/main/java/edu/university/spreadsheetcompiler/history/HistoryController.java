package edu.university.spreadsheetcompiler.history;

import edu.university.spreadsheetcompiler.submission.SubmissionDtos.HistoryDetailResponse;
import edu.university.spreadsheetcompiler.submission.SubmissionDtos.HistoryListResponse;
import edu.university.spreadsheetcompiler.submission.SubmissionService;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final SubmissionService submissionService;

    public HistoryController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    public HistoryListResponse history(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return submissionService.list(principal.getName(), page, size);
    }


    @GetMapping("/{submissionId}")
    public HistoryDetailResponse detail(Principal principal, @PathVariable Long submissionId) {
        return submissionService.detail(principal.getName(), submissionId);
    }
}

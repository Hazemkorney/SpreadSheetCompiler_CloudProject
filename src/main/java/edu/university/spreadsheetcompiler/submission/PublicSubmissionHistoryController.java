package edu.university.spreadsheetcompiler.submission;
import java.security.Principal;
import edu.university.spreadsheetcompiler.submission.SubmissionDtos.HistoryResponse;
import edu.university.spreadsheetcompiler.submission.SubmissionDtos.HistoryListResponse;
import edu.university.spreadsheetcompiler.submission.SubmissionDtos.SubmitRequest;
import edu.university.spreadsheetcompiler.submission.SubmissionDtos.SubmitResponse;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;

@RestController
public class PublicSubmissionHistoryController {

    private final SubmissionService submissionService;

    public PublicSubmissionHistoryController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public SubmitResponse submit(
            Principal principal,
            @RequestBody SubmitRequest request
    ) {
        return submissionService.submit(principal.getName(), request);
    }

 
    @GetMapping("/history")
public HistoryListResponse history(
        Principal principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
) {
    if (principal == null) {
        throw new RuntimeException("Not authenticated");
    }

    return submissionService.list(
            principal.getName(),
            page,
            size
    );
}
    

}
package edu.university.spreadsheetcompiler.submission;



import edu.university.spreadsheetcompiler.submission.SubmissionDtos.CreateSubmissionRequest;

import edu.university.spreadsheetcompiler.submission.SubmissionDtos.CreateSubmissionResponse;

import java.security.Principal;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;



@RestController

@RequestMapping("/api/submissions")

public class SubmissionController {

    private final SubmissionService submissionService;



    public SubmissionController(SubmissionService submissionService) {

        this.submissionService = submissionService;

    }



    @PostMapping

    public CreateSubmissionResponse create(Principal principal, @RequestBody CreateSubmissionRequest request) {

        return submissionService.create(principal.getName(), request);

    }

}
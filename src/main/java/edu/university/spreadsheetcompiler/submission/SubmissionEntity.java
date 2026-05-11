package edu.university.spreadsheetcompiler.submission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "submissions")
public class SubmissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 1024)
    private String formula;

    @Column(nullable = false)
    private String status;

     
    @Column
    private Double result; 
    

    @Column(columnDefinition = "TEXT")
    private String tokensJson;

    @Column(columnDefinition = "TEXT")
    private String astJson;

    @Column(columnDefinition = "TEXT")
    private String errorsJson;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

   
    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }
    

    public String getTokensJson() { return tokensJson; }
    public void setTokensJson(String tokensJson) { this.tokensJson = tokensJson; }

    public String getAstJson() { return astJson; }
    public void setAstJson(String astJson) { this.astJson = astJson; }

    public String getErrorsJson() { return errorsJson; }
    public void setErrorsJson(String errorsJson) { this.errorsJson = errorsJson; }

    public Instant getCreatedAt() { return createdAt; }
}
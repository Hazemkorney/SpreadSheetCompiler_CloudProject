
package edu.university.spreadsheetcompiler.submission;

import java.util.Optional;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;



public interface SubmissionRepository extends JpaRepository<SubmissionEntity , Long> {

    Page<SubmissionEntity> findByUsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    Optional<SubmissionEntity> findByIdAndUsername(Long id, String username);

    Page<SubmissionEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
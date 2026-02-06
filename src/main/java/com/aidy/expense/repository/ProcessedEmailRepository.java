package com.aidy.expense.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aidy.expense.entity.ProcessedEmail;

@Repository
public interface ProcessedEmailRepository extends JpaRepository<ProcessedEmail, String> {
    // existsById is provided by default, so no extra code needed here
}
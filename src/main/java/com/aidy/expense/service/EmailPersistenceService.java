package com.aidy.expense.service;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.aidy.expense.entity.ProcessedEmail;
import com.aidy.expense.exception.ServiceAPIException;
import com.aidy.expense.repository.ProcessedEmailRepository;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailPersistenceService {

  @Retryable(retryFor = {SQLServerException.class, JDBCConnectionException.class}, maxAttempts = 3,
      backoff = @Backoff(delay = 5000))
  public void saveProcessedData(ProcessedEmailRepository repository, ProcessedEmail entity) {
    log.info("Attempting to save transaction {} to MS SQL...", entity.getMessageId());
    repository.save(entity);
  }

  @Retryable(retryFor = {SQLServerException.class, JDBCConnectionException.class}, maxAttempts = 3,
      backoff = @Backoff(delay = 5000))
  public void checkDuplicateProcessing(ProcessedEmailRepository emailRepository,
      String dbEmailId) {
    log.info("Attempting to check duplicate transaction: {}", dbEmailId);
    if (emailRepository.existsById(dbEmailId)) {
      throw new ServiceAPIException("Duplicate Transaction: Email with Message-ID "
          + dbEmailId + " has already been processed.", HttpStatus.CONFLICT);
    }
  }

  @Recover
  public void recoverDuplicateCheck(Exception e, ProcessedEmailRepository repo,
      String dbEmailId) {
    log.error("Duplicate check failed for {}. DB unreachable: {}", dbEmailId,
        e.getMessage());
    if (e instanceof ServiceAPIException serviceException) {
      throw serviceException;
    }
    throw new ServiceAPIException("Database unavailable for duplicate check: " + e.getMessage(),
        HttpStatus.SERVICE_UNAVAILABLE
    );
  }

  @Recover
  public void recoverSave(Exception e, ProcessedEmailRepository repo, ProcessedEmail entity) {
    log.error("Save failed for {}. DB unreachable: {}", entity.getMessageId(), e.getMessage());
    throw new ServiceAPIException("Database unavailable for save entity",
        HttpStatus.SERVICE_UNAVAILABLE);
  }
}

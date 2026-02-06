package com.aidy.expense.service;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.aidy.expense.dto.EmailRequestBody;
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
      EmailRequestBody request) {
    log.info("Attempting to check duplicate transaction: {}", request.getMessageId());
    if (emailRepository.existsById(request.getMessageId())) {
      throw new ServiceAPIException("Duplicate Transaction: Email with Message-ID "
          + request.getMessageId() + " has already been processed.", HttpStatus.CONFLICT);
    }
  }

  @Recover
  public void recoverDuplicateCheck(Exception e, ProcessedEmailRepository repo,
      EmailRequestBody request) {
    log.error("Duplicate check failed for {}. DB unreachable: {}", request.getMessageId(),
        e.getMessage());
    throw new ServiceAPIException("Database unavailable for duplicate check",
        HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Recover
  public void recoverSave(Exception e, ProcessedEmailRepository repo, ProcessedEmail entity) {
    log.error("Save failed for {}. DB unreachable: {}", entity.getMessageId(), e.getMessage());
    throw new ServiceAPIException("Database unavailable for save entity",
        HttpStatus.SERVICE_UNAVAILABLE);
  }
}

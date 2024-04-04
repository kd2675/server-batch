package com.example.batch.service.batch.database.rep.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BatchStepExecutionREP extends JpaRepository<BatchStepExecution, Long> {
    List<BatchStepExecution> findAllByStartTimeBefore(LocalDateTime startTime);
}
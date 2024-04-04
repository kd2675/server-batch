package com.example.batch.service.batch.database.rep.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobExecutionREP extends JpaRepository<BatchJobExecution, Long> {
}
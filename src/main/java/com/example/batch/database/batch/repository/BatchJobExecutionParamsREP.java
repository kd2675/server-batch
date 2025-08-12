package com.example.batch.database.batch.repository;

import com.example.batch.database.batch.entity.BatchJobExecutionParams;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobExecutionParamsREP extends JpaRepository<BatchJobExecutionParams, Long> {
}
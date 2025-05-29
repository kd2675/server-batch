package com.example.batch.cron.database.rep.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobExecutionContextREP extends JpaRepository<BatchJobExecutionContext, Long> {
}
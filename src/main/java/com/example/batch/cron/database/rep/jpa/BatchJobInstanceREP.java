package com.example.batch.cron.database.rep.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobInstanceREP extends JpaRepository<BatchJobInstance, Long> {
}
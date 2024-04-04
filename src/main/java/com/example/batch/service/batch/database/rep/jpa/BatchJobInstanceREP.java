package com.example.batch.service.batch.database.rep.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobInstanceREP extends JpaRepository<BatchJobInstance, Long> {
}
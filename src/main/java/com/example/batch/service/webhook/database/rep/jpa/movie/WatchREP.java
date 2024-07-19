package com.example.batch.service.webhook.database.rep.jpa.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WatchREP extends JpaRepository<WatchEntity, Long> {
    @Query(value = "select e from WatchEntity e order by rand() limit 1")
    Optional<WatchEntity> findWatchRand();
}
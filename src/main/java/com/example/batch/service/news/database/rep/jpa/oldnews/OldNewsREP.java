package com.example.batch.service.news.database.rep.jpa.oldnews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldNewsREP extends JpaRepository<OldNewsEntity, Long> {
}

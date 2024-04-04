package com.example.batch.service.news.database.rep.jpa.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsREP extends JpaRepository<NewsEntity, Long> {
    List<NewsEntity> findTop10By();
    List<NewsEntity> findTop15BySendYnOrderByIdDesc(String sendYn);
}

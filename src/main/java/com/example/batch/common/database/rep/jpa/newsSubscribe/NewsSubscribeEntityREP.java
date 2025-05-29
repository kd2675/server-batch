package com.example.batch.common.database.rep.jpa.newsSubscribe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsSubscribeEntityREP extends JpaRepository<NewsSubscribeEntity, Long> {
}
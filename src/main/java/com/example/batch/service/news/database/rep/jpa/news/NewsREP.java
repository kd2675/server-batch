package com.example.batch.service.news.database.rep.jpa.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface NewsREP extends JpaRepository<NewsEntity, Long> {
    List<NewsEntity> findTop10By();
    List<NewsEntity> findTop15BySendYnOrderByIdDesc(String sendYn);
    List<NewsEntity> findTop15BySendYnAndCategoryInOrderByIdDesc(
            @NonNull String sendYn,
            @NonNull Collection<String> categories
    );
    List<NewsEntity> findBySendYnAndCreateDateAfterAndCategoryInOrderByIdDesc(
            @NonNull String sendYn,
            @NonNull LocalDateTime createDate,
            @NonNull Collection<String> categories
    );

}

package com.example.batch.common.database.rep.jpa.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    List<NewsEntity> findBySendYnAndCategoryInAndCreateDateAfterOrderByIdDesc(
            @NonNull String sendYn,
            @NonNull Collection<String> categories,
            @NonNull LocalDateTime createDate
    );

    Page<NewsEntity> findAll(Specification<NewsEntity> spec, Pageable pageable);

}

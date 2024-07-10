package com.example.batch.service.news.database.rep.jpa.oldnews;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OldNewsREP extends JpaRepository<OldNewsEntity, Long> {
    List<OldNewsEntity> findByTitleLikeOrContentLikeOrderByIdDesc(@Nullable String title, @Nullable String content, Pageable pageable);

    @Query(value = "SELECT e.* FROM OLD_NEWS e WHERE regexp_like(e.title, :text)", nativeQuery = true)
    List<OldNewsEntity> search(@Param("text") String text, Pageable pageable);
}

package com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface MattermostSentREP extends JpaRepository<MattermostSentEntity, Long> {
    long deleteByCategory(@NonNull String category);
}

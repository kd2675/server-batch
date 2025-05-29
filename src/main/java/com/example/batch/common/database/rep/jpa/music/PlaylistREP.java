package com.example.batch.common.database.rep.jpa.music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PlaylistREP extends JpaRepository<PlaylistEntity, Long> {
    Optional<PlaylistEntity> findByNo(@NonNull Long no);

}
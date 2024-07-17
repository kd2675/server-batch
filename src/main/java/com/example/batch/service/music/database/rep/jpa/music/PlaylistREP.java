package com.example.batch.service.music.database.rep.jpa.music;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface PlaylistREP extends JpaRepository<PlaylistEntity, Long> {
    Optional<PlaylistEntity> findByNo(@NonNull Long no);

}
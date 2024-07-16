package com.example.batch.service.music.database.rep.jpa.music;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistREP extends JpaRepository<PlaylistEntity, Long> {
}
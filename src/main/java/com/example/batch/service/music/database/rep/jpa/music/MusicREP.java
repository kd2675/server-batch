package com.example.batch.service.music.database.rep.jpa.music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MusicREP extends JpaRepository<MusicEntity, Long> {
    MusicEntity findTop1ByNoOrderByIdDesc(Long no);

    @Query(value = "select e from MusicEntity e order by rand() limit 1")
    Optional<MusicEntity> findMusicRand();

}
package com.example.batch.common.database.rep.jpa.music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface MusicREP extends JpaRepository<MusicEntity, Long> {
    Optional<MusicEntity> findTop1ByIdOrderByIdDesc(Long id);
    Optional<MusicEntity> findTop1ByNoOrderByIdDesc(Long no);

    @Query(value = "select e from MusicEntity e order by rand() limit 1")
    Optional<MusicEntity> findMusicRand();

    Optional<MusicEntity> findBySlctAndNo(@Nullable String slct, @Nullable Long no);


}
package com.example.batch.service.music.database.rep.jpa.music;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.database.common.rep.jpa.CommonDateEntity;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@Table(name = "MUSIC_TB")
public class MusicEntity extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "album", nullable = true, length = 255)
    private String album;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "singer", nullable = false, length = 255)
    private String singer;

    @Column(name = "lyrics", nullable = true, length = 255)
    private String lyrics;

    @Column(name = "pubDate", nullable = true)
    private LocalDate pubDate;
}

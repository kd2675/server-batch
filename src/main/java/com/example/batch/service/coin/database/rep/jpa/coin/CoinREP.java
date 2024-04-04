package com.example.batch.service.coin.database.rep.jpa.coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoinREP extends JpaRepository<CoinEntity, Long> {
    public List<CoinEntity> findByCreateDateBefore(LocalDateTime dateTime);
    List<CoinEntity> findTop10ByOrderByIdDesc();
    List<CoinEntity> findTop1ByOrderByIdDesc();
}

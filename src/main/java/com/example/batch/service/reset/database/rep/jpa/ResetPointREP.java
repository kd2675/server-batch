package com.example.batch.service.reset.database.rep.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ResetPointREP extends JpaRepository<ResetPoint, Long> {
    List<ResetPoint> findByResetYnAndPointIdInOrderByCreateDateDesc(@NonNull String resetYn, @NonNull Collection<Integer> pointIds);
}
package com.example.batch.service.reset.database.rep.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ResetPointREP extends JpaRepository<ResetPointEntity, Long> {
    List<ResetPointEntity> findByResetYnAndPointIdInOrderByCreateDateDesc(@NonNull String resetYn, @NonNull Collection<Integer> pointIds);
}
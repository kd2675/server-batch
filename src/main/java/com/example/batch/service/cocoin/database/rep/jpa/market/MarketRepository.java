package com.example.batch.service.cocoin.database.rep.jpa.market;

import org.example.database.auth.database.rep.jpa.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Optional;

public interface MarketRepository extends JpaRepository<MarketEntity, Long> {
    Optional<MarketEntity> findByUserEntityInAndCoinSlctInAndMarginSlctIn(@NonNull Collection<UserEntity> userEntities, @NonNull Collection<String> coinSlcts, @NonNull Collection<String> marginSlcts);
    Optional<MarketEntity> findByUserEntityIn(@NonNull Collection<UserEntity> userEntities);

}
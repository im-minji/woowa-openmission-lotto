package com.woowa.lotto.repository;

import com.woowa.lotto.domain.PurchasedLotto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasedLottoRepository extends JpaRepository<PurchasedLotto, Long> {
    List<PurchasedLotto> findAllByPurchaseDateBetween(LocalDate start, LocalDate end);
}

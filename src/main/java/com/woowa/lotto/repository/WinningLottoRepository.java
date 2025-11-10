package com.woowa.lotto.repository;

import com.woowa.lotto.domain.WinningLotto;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WinningLottoRepository extends JpaRepository<WinningLotto, Long> {
    Optional<WinningLotto> findByDrawDate(LocalDate drawDate);
}

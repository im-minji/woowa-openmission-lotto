package com.woowa.lotto.repository;

import com.woowa.lotto.domain.MyLotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyLottoRepository extends JpaRepository<MyLotto, Long> {
}

package com.woowa.lotto.service;

import com.woowa.lotto.domain.Lotto;
import com.woowa.lotto.domain.MyLotto;
import com.woowa.lotto.repository.LottoRepository;
import com.woowa.lotto.repository.MyLottoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LottoServiceTest {
    // 1. @Autowired: 스프링이 '자동'으로 만든 '진짜' LottoService Bean을 주입
    @Autowired
    private LottoService lottoService;

    @Autowired
    private MyLottoRepository myLottoRepository;

    @Autowired
    private LottoRepository lottoRepository;

    @DisplayName("generateRandomLotto() 메서드가 6개의 숫자를 가진 Lotto 객체를 성공적으로 생성한다.")
    @Test
    void generateRandomLotto_test() {
        Lotto randomLotto = lottoService.generateRandomLotto();

        assertThat(randomLotto).isNotNull();

        assertThat(randomLotto.getNumbers()).isNotNull();
        assertThat(randomLotto.getNumbers().size()).isEqualTo(6);
    }

    // --- (TODO: 화요일의 계획 2단계) ---
    // 'saveToMyLotto_test()' 메서드를 추가
}
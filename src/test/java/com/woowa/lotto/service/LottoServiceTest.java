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
    @DisplayName("saveToMyLotto()가 'Lotto'(엔진)와 'MyLotto'(본체)를 DB에 '동시' 저장한다.")
    @Test
    void saveToMyLotto_test() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        String lottoName = "나의 행운 번호";

        MyLotto savedMyLotto = lottoService.saveToMyLotto(numbers, lottoName);


        assertThat(savedMyLotto.getId()).isNotNull(); // ID 발급 확인

        // Repository를 이용해 DB에 로또 저장되었는 지
        Optional<MyLotto> foundMyLottoOptional = myLottoRepository.findById(savedMyLotto.getId());

        assertThat(foundMyLottoOptional).isPresent(); // DB에서 찾아왔는가?

        MyLotto foundMyLotto = foundMyLottoOptional.get();
        assertThat(foundMyLotto.getLottoName()).isEqualTo(lottoName); // 이름 일치

        assertThat(foundMyLotto.getMyLotto()).isNotNull();
        Long lottoEngineId = foundMyLotto.getMyLotto().getId();
        assertThat(lottoEngineId).isNotNull();

        Optional<Lotto> foundLottoEngineOpt = lottoRepository.findById(lottoEngineId);
        assertThat(foundLottoEngineOpt).isPresent(); // "lotto 테이블에도 저장되었는가?"

        // '타입'까지 비교하는 isEqualTo() 대신, '내용물'만 비교하는 contains...()로 변경
        assertThat(foundLottoEngineOpt.get().getNumbers())
                .containsExactlyInAnyOrderElementsOf(numbers);
    }
}
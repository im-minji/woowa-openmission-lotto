package com.woowa.lotto.repository;

import com.woowa.lotto.domain.Lotto;
import com.woowa.lotto.domain.MyLotto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// H2 같은 인메모리 DB를 자동으로 실행하고, 끝나면 자동 롤백
@DataJpaTest
class MyLottoRepositoryTest {

    // 테스트에 필요한 repository 2개 추가: MyLotto를 저장하려면 '부품'인 Lotto도 저장해야 하므로 2개 다 필요
    @Autowired
    private MyLottoRepository myLottoRepository;

    @Autowired
    private LottoRepository lottoRepository;

    @DisplayName("MyLotto 객체를 저장하고, ID로 다시 찾을 수 있다.")
    @Test
    void saveAndFindById() {
        Lotto lottoEngine = new Lotto(List.of(1, 2, 3, 4, 5, 6));
        Lotto savedLottoEngine = lottoRepository.save(lottoEngine);

        MyLotto myLotto = new MyLotto(savedLottoEngine, "나의 행운 번호");

        MyLotto savedMyLotto = myLottoRepository.save(myLotto);

        Optional<MyLotto> foundMyLottoOptional = myLottoRepository.findById(savedMyLotto.getId());

        assertThat(foundMyLottoOptional).isPresent();

        MyLotto foundMyLotto = foundMyLottoOptional.get();
        assertThat(foundMyLotto.getLottoName()).isEqualTo("나의 행운 번호");

        assertThat(foundMyLotto.getMyLotto().getId()).isEqualTo(savedLottoEngine.getId());
    }
}
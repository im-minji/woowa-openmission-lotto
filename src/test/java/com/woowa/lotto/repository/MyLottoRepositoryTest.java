package com.woowa.lotto.repository;

import com.woowa.lotto.domain.Lotto;
import com.woowa.lotto.domain.MyLotto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager; // [추가]

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// H2 같은 인메모리 DB를 자동으로 실행하고, 끝나면 자동 롤백
@DataJpaTest
class MyLottoRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MyLottoRepository myLottoRepository;

    @DisplayName("MyLotto 객체를 저장하고, ID로 다시 찾을 수 있다. (@Embeddable 적용)")
    @Test
    void saveAndFindById() {
        // 1. Lotto 값 객체 생성 (LottoRepository에 저장 X)
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        Lotto lottoEngine = new Lotto(numbers);

        // 2. MyLotto 엔티티 생성 시, Lotto 값 객체를 '포함'
        MyLotto myLotto = new MyLotto(lottoEngine, "나의 행운 번호");

        // MyLotto만 저장하면 @Embedded된 Lotto가 함께 저장
        MyLotto savedMyLotto = myLottoRepository.save(myLotto);

        entityManager.flush();
        entityManager.clear();

        Optional<MyLotto> foundMyLottoOptional = myLottoRepository.findById(savedMyLotto.getId());

        assertThat(foundMyLottoOptional).isPresent();

        MyLotto foundMyLotto = foundMyLottoOptional.get();

        // 1. 이름 검증
        assertThat(foundMyLotto.getLottoName()).isEqualTo("나의 행운 번호");

        // 2. 포함된 Lotto 객체 검증
        assertThat(foundMyLotto.getMyLotto()).isNotNull();

        // 3. Lotto의 ID(X) 대신 실제 값(numbers)을 검증
        assertThat(foundMyLotto.getMyLotto().getNumbers())
                .isNotNull()
                .hasSize(6)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }
}
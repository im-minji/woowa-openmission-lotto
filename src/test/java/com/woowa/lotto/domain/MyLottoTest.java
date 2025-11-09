package com.woowa.lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MyLottoTest {

    Lotto validLotto = new Lotto(List.of(1, 2, 3, 4, 5, 6));

    @DisplayName("로또가 비워져있으면 myLotto에 등록할 수 없습니다.")
    @Test
    void 로또가_null_이면_myLotto에_추가할_시_예외가_발생한다() {
        assertThatThrownBy(() -> new MyLotto(null, "내 로또"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("[ERROR] 로또가 비워져 있습니다.");
    }

    @DisplayName("로또 이름이 20글자 이상이거나 비워져있으면 myLotto에 등록할 수 없습니다.")
    @Test
    void 로또이름이_20글자_이상이거나_null_이면_myLotto에_추가할_시_예외가_발생한다() {
        assertThatThrownBy(() -> new MyLotto(validLotto, "내 로또1234567891011121314151617181920"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new MyLotto(validLotto, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("[ERROR] 로또 이름은 null일 수 없습니다.");

        assertThatThrownBy(() -> new MyLotto(validLotto, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 로또 이름은 1~20글자 범위로 입력해주세요.");
    }

    @DisplayName("정상적인 로또와 이름으로 myLotto가 생성된다.")
    @Test
    void 정상적인_로또와_이름으로_myLotto가_생성된다() {
        String testLottoName = "나만의 로또";
        MyLotto myLotto = new MyLotto(validLotto, testLottoName);

        assertThat(myLotto.getMyLotto()).isEqualTo(validLotto);
        assertThat(myLotto.getLottoName()).isEqualTo(testLottoName);
    }
}

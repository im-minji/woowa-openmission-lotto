package com.woowa.lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LottoTest {
    @Test
    void 로또_번호의_개수가_6개가_넘어가면_예외가_발생한다() {
        assertThatThrownBy(() -> new Lotto(List.of(1, 2, 3, 4, 5, 6, 7)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("로또 번호에 중복된 숫자가 있으면 예외가 발생한다.")
    @Test
    void 로또_번호에_중복된_숫자가_있으면_예외가_발생한다() {
        assertThatThrownBy(() -> new Lotto(List.of(1, 2, 3, 4, 5, 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // TODO: 추가 기능 구현에 따른 테스트 코드 작성

    // 로또 숫자 범위 1~45 확인
    @DisplayName("로또 번호에 1~45 범위에 없는 숫자가 있으면 예외가 발생한다")
    @Test
    void 로또_번호에_1보다_작고_45보다_큰_숫자가_있으면_예외가_발생한다() {
        assertThatThrownBy(() -> new Lotto(List.of(0, 1, 2, 3, 4, 5)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Lotto(List.of(1, 2, 3, 4, 5, 46)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // 보너스 번호가 리스트 중에 존재하는지 확인해서 true/false 를 돌려주는 메서드
    // 객체가 데이터를 가지고 스스로 일하는지를 검증
    @DisplayName("hasBonusNum 메서드가 숫자를 올바르게 확인한다")
    @Test
    void 보너스_번호가_로또_번호_리스트에_포함되어_있으면_boolean_값을_돌려준다() {
        Lotto lotto = new Lotto(List.of(1, 2, 3, 4, 5, 6));

        boolean return_true = lotto.hasBonusNum(6);
        assertThat(return_true).isTrue();

        boolean return_false = lotto.hasBonusNum(10);
        assertThat(return_false).isFalse();
    }
}

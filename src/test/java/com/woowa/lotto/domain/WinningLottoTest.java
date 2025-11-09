package com.woowa.lotto.domain;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class WinningLottoTest {
    private Lotto testWinningLotto;
    private LocalDate drawDate;

    @BeforeEach
    void setUp() {
        // 테스트에서 공통으로 사용할 '기본 당첨 번호' Lotto 객체를 미리 생성
        testWinningLotto = new Lotto(List.of(1, 2, 3, 4, 5, 6));
        drawDate = LocalDate.now();
    }

    @DisplayName("보너스 번호가 1~45 범위에 없는 숫자라면 예외가 발생한다.")
    @Test
    void 보너스_번호가_1보다_작거나_45보다_크면_예외가_발생한다() {
        assertThatThrownBy(() -> new WinningLotto(testWinningLotto, 46, drawDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 보너스 번호의 범위는 1~45여야 합니다.");

        assertThatThrownBy(() -> new WinningLotto(testWinningLotto, 0, drawDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 보너스 번호의 범위는 1~45여야 합니다.");
    }

    @DisplayName("보너스 번호가 로또 당첨와 중복되면 예외가 발생한다.")
    @Test
    void 보너스_번호가_당첨_번호와_중복되면_예외가_발생한다() {
        assertThatThrownBy(() -> new WinningLotto(testWinningLotto, 6, drawDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 보너스 번호는 당첨 번호와 중복되면 안됩니다.");
    }

    @DisplayName("정상적인 당첨 번호와 보너스 번호로 WinningLotto가 생성된다.")
    @Test
    void 당첨_번호와_보너스_번호가_정상적으로_WinningLotto가_생성된다() {
        int testBonusNum = 7;
        WinningLotto winningLotto = new WinningLotto(testWinningLotto, testBonusNum, drawDate);

        assertThat(winningLotto.getWinningLotto()).isEqualTo(testWinningLotto);
        assertThat(winningLotto.getBonusNum()).isEqualTo(testBonusNum);
        assertThat(winningLotto.getDrawDate()).isEqualTo(drawDate);
    }


}

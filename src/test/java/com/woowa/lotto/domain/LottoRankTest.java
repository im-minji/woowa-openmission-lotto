package com.woowa.lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class LottoRankTest {

    @DisplayName("일치 개수와 보너스 여부에 따라 정확한 랭크(당첨 순위)을 반환해야 한다.")
    @ParameterizedTest
    @CsvSource({
            "6, false, FIRST",  // 1등
            "6, true, FIRST",   // 1등 (보너스 여부 상관 없음)
            "5, true, SECOND",  // 2등
            "5, false, THIRD",  // 3등
            "4, true, FOURTH",  // 4등 (보너스 여부 상관 없음)
            "4, false, FOURTH", // 4등
            "3, true, FIFTH",   // 5등 (보너스 여부 상관 없음)
            "3, false, FIFTH",  // 5등
            "2, true, NONE",    // 꽝
            "2, false, NONE",   // 꽝
            "1, false, NONE",   // 꽝
            "0, false, NONE"    // 꽝
    })
    void 정확한_당첨_순위를_반환해야_한다(int winningCount, boolean hasBonus, LottoRank expectedRank) {
        LottoRank actualRank = LottoRank.find(winningCount, hasBonus);
        assertThat(actualRank).isEqualTo(expectedRank);
    }

    @Test
    @DisplayName("FIRST(1등) Enum 상수가 올바른 상금과 일치 개수를 가지고 있는지 확인한다.")
    void 일등이_올바른_상금과_당첨_번호와_6개_일치하지_않으면_예외가_발생한다() {
        LottoRank first = LottoRank.FIRST;
        assertThat(first.getWinningCount()).isEqualTo(6);
        assertThat(first.getWinningPrize()).isEqualTo(2_000_000_000L);
        assertThat(first.isNeedBonus()).isFalse();
    }

    @Test
    @DisplayName("SECOND(2등) Enum 상수가 올바른 상금, 일치 개수, 보너스 필요 여부를 가지고 있는지 확인한다.")
    void 이등이_올바른_상금과_보너스_번호_여부와_당첨_번호와_5개_일치하지_않으면_예외가_발생한다() {
        LottoRank second = LottoRank.SECOND;

        assertThat(second.getWinningCount()).isEqualTo(5);
        assertThat(second.getWinningPrize()).isEqualTo(30_000_000L);
        assertThat(second.isNeedBonus()).isTrue();
    }

    @Test
    @DisplayName("NONE(꽝) Enum 상수가 올바른 상금과 일치 개수를 가지고 있는지 확인한다.")
    void 꽝이_상금이_있고_당첨_번호와_3개_이상_일치시_예외가_발생한다() {
        LottoRank none = LottoRank.NONE;

        assertThat(none.getWinningCount()).isEqualTo(0);
        assertThat(none.getWinningPrize()).isEqualTo(0L);
        assertThat(none.isNeedBonus()).isFalse();
    }
}

package com.woowa.lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.offset;

class LottoResultTest {

    @DisplayName("생성자에 null 통계 맵이 전달되면 예외가 발생한다.")
    @Test
    void 생성자는_null_맵을_허용하지_않는다() {
        assertThatThrownBy(() -> new LottoResult(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("[ERROR] 통계판이 비워져 있습니다.");
    }

    @DisplayName("5등 1장, 8000원 구매 시 수익률 62.5%를 반환한다.")
    @Test
    void 수익률계산_5등_1장_8000원() {
        Map<LottoRank, Integer> statisticsMap = new EnumMap<>(LottoRank.class);
        statisticsMap.put(LottoRank.FIRST, 0);
        statisticsMap.put(LottoRank.SECOND, 0);
        statisticsMap.put(LottoRank.THIRD, 0);
        statisticsMap.put(LottoRank.FOURTH, 0);
        statisticsMap.put(LottoRank.FIFTH, 1); // 5등 1개
        statisticsMap.put(LottoRank.NONE, 7);  // 꽝 7개 (총 8000원 구매 가정)

        int lottoPrice = 8000;

        LottoResult lottoResult = new LottoResult(statisticsMap);
        double rateOfReturn = lottoResult.getRateOfReturn(lottoPrice);

        assertThat(rateOfReturn).isEqualTo(62.5, offset(0.001));
    }

    @DisplayName("당첨금이 0원일 때 0.0%를 반환한다.")
    @Test
    void 수익률계산_당첨금_0원() {
        Map<LottoRank, Integer> statisticsMap = new EnumMap<>(LottoRank.class);
        statisticsMap.put(LottoRank.NONE, 1); // 꽝 1개
        int lottoPrice = 1000;

        LottoResult lottoResult = new LottoResult(statisticsMap);
        double rateOfReturn = lottoResult.getRateOfReturn(lottoPrice);

        assertThat(rateOfReturn).isEqualTo(0.0);
    }

    @DisplayName("getStatistics()가 수정 불가능한 맵을 반환한다.")
    @Test
    void getStatistics_는_수정불가능한_맵을_반환한다() {
        Map<LottoRank, Integer> statisticsMap = new EnumMap<>(LottoRank.class);
        statisticsMap.put(LottoRank.FIFTH, 1);

        LottoResult lottoResult = new LottoResult(statisticsMap);

        Map<LottoRank, Integer> unmodifiableMap = lottoResult.getStatistics();

        assertThatThrownBy(() -> unmodifiableMap.put(LottoRank.FIRST, 1)) // 맵 수정을 시도하면
                .isInstanceOf(UnsupportedOperationException.class); // 예외가 발생해야 함
    }
}

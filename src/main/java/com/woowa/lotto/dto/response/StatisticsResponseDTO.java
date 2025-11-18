package com.woowa.lotto.dto.response;

import com.woowa.lotto.domain.LottoRank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class StatisticsResponseDTO {

    // 추첨일
    private final LocalDate drawDate;

    // 해당 주차의 당첨 번호
    private final List<Integer> winningNumbers;

    // 보너스 번호
    private final Integer bonusNum;

    // 등수별 당첨 횟수
    private final Map<LottoRank, Integer> rankCounts;

    // 수익률
    private final double rateOfReturn;
}
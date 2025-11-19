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
    private final LocalDate drawDate;  // 추첨일
    private final List<Integer> winningNumbers;  // 해당 주차의 당첨 번호
    private final Integer bonusNum;  // 보너스 번호
    private final Map<LottoRank, Integer> rankCounts;  // 등수별 당첨 횟수
    private final double rateOfReturn;  // 수익률

    // 1. 총 구매 금액
    private final long totalPurchaseAmount;

    // 2. 총 당첨 금액
    private final long totalWinningMoney;

    // 3. 개별 로또 상세 결과 리스트
    private final List<LottoResultResponseDTO> lottoResults;
}
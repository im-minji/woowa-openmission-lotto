package com.woowa.lotto.domain;

import java.util.Collections;

import java.util.Map;
import java.util.Objects;

public class LottoResult {
    private static final int MIN_PURCHASE_PRICE = 0;
    private static final double DEFAULT_RATE_OF_RETURN = 0.0;
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    private final Map<LottoRank, Integer> statistics;

    public LottoResult(Map<LottoRank, Integer> statistics) {
        this.statistics = Objects.requireNonNull(statistics, "[ERROR] 통계판이 비워져 있습니다.");
    }

    // 2. 총상금 계산 (private long calculateTotalWinningPrize())
    private double calculateTotalWinningPrize() {
        double totalWinningPrize = 0;

        for (Map.Entry<LottoRank, Integer> entry : this.statistics.entrySet()) {
            long prize = entry.getKey().getWinningPrize();
            int count = entry.getValue();
            totalWinningPrize += (double) prize * (double) count;
        }
        return totalWinningPrize;
    }

    public double getRateOfReturn(int purchaseAmount) {
        double totalWinningPrize = calculateTotalWinningPrize();

        if (purchaseAmount == MIN_PURCHASE_PRICE) {
            return DEFAULT_RATE_OF_RETURN;
        }
        return (totalWinningPrize / (double)purchaseAmount) * PERCENTAGE_MULTIPLIER;
    }

    public Map<LottoRank, Integer> getStatistics() {
        return Collections.unmodifiableMap(this.statistics);
    }
}
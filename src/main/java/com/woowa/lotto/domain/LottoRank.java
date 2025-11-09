package com.woowa.lotto.domain;

import java.util.Arrays;

public enum LottoRank {
    FIRST(6, 2000000000L, false),
    SECOND(5, 30000000L, true),
    THIRD(5, 1500000L, false),
    FOURTH(4, 50000L, false),
    FIFTH(3, 5000L, false),
    NONE(0, 0, false);

    private final int winningCount;
    private final long winningPrize;
    private final boolean isNeedBonus;

    LottoRank(int winningCount, long winningPrize, boolean needBonus) {
        this.winningCount = winningCount;
        this.winningPrize = winningPrize;
        this.isNeedBonus = needBonus;
    }

    public boolean matches(int count, boolean bonus) {
        // 'FIRST', 'FOURTH', 'FIFTH' 는 보너스 여부에 관심이 없다.
        if (this.winningCount != 5) {
            return this.winningCount == count; // 보너스 있고 없고를 전달할 필요가 없음
        }

        // 'SECOND', 'THIRD' (5개인 경우)
        return this.winningCount == count && this.isNeedBonus == bonus;
    }

    public static LottoRank find(int winningCount, boolean hasBonus) {
        return Arrays.stream(LottoRank.values()) // 모든 Rank 상수를 가져와서
                .filter(rank -> rank.matches(winningCount, hasBonus)) // 각자 matches?
                .findAny() // "true"라고 대답한 랭크를 찾아서 반환
                .orElse(LottoRank.NONE); // 아무도 없으면 NONE을 반환
    }

    public int getWinningCount() {return winningCount;}
    public long getWinningPrize() {return winningPrize;}
    public boolean isNeedBonus() {return isNeedBonus;}
}
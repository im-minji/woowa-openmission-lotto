package com.woowa.lotto.domain;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PurchasedLottoTest {

    private Lotto validLotto;
    private LocalDate validPurchaseDate;

    @BeforeEach // 모든 테스트 전에 '초기화'
    void setUp() {
        validLotto = new Lotto(List.of(1, 2, 3, 4, 5, 6));
        validPurchaseDate = LocalDate.now();
    }

    @DisplayName("로또가 비워져있으면 PurchasedLotto에 등록할 수 없습니다.")
    @Test
    void 로또가_null_이면_PurchasedLotto에_추가할_시_예외가_발생한다() {
        assertThatThrownBy(() -> new PurchasedLotto(null, validPurchaseDate))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("[ERROR] 로또가 비워져 있습니다.");
    }

    @DisplayName("날짜가 비워져있으면 PurchasedLotto에 등록할 수 없습니다.")
    @Test
    void 날짜가_null_이면_PurchasedLotto에_추가할_시_예외가_발생한다() {
        assertThatThrownBy(() -> new PurchasedLotto(validLotto, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("[ERROR] 날짜가 비워져 있습니다.");
    }

    @DisplayName("정상적인 로또와 날짜로 PurchasedLotto가 생성된다.")
    @Test
    void 정상적인_로또와_날짜로_purchasedLotto가_생성된다() {
        PurchasedLotto purchasedLotto = new PurchasedLotto(validLotto, validPurchaseDate);

        assertThat(purchasedLotto.getPurchasedLotto()).isEqualTo(validLotto);
        assertThat(purchasedLotto.getPurchaseDate()).isEqualTo(validPurchaseDate);
    }

    @DisplayName("isPurchasedInWeek()가 같은 주, 다른 주를 올바르게 구별한다.")
    @Test
    void isPurchasedInWeek_메서드가_주를_올바르게_구분한다() {
        LocalDate myPurchaseDate = LocalDate.of(2025, 11, 3); // 월요일
        LocalDate sameWeekDate = LocalDate.of(2025, 11, 5);   // 수요일 (같은 주 O)
        LocalDate diffWeekDate = LocalDate.of(2025, 11, 10);  // 다음 주 월요일 (같은 주 X)
        LocalDate diffYearDate = LocalDate.of(2024, 11, 3);   // 작년 (같은 주 X)

        PurchasedLotto purchasedLotto = new PurchasedLotto(validLotto, myPurchaseDate);

        // 같은 주 테스트
        assertThat(purchasedLotto.isPurchasedInWeek(sameWeekDate)).isTrue();
        // 자기 자신 테스트
        assertThat(purchasedLotto.isPurchasedInWeek(myPurchaseDate)).isTrue();
        // 다른 주 테스트
        assertThat(purchasedLotto.isPurchasedInWeek(diffWeekDate)).isFalse();
        // 다른 연도 테스트
        assertThat(purchasedLotto.isPurchasedInWeek(diffYearDate)).isFalse();
    }
}

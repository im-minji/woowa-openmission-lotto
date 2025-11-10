package com.woowa.lotto.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.time.temporal.WeekFields;
import java.util.Locale;
import jakarta.persistence.*;

@Entity
public class PurchasedLotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lotto_id")
    private final Lotto purchasedLotto;

    @Column
    private final LocalDate purchaseDate;

    protected  PurchasedLotto() {
        this.purchasedLotto = null;
        this.purchaseDate = null;
    }

    public PurchasedLotto(Lotto purchasedLotto, LocalDate purchaseDate) {
        this.purchasedLotto = Objects.requireNonNull(purchasedLotto, "[ERROR] 로또가 비워져 있습니다.");
        this.purchaseDate = Objects.requireNonNull(purchaseDate, "[ERROR] 날짜가 비워져 있습니다.");
    }

    public boolean isPurchasedInWeek(LocalDate dateToCompare) {
        WeekFields weekRule = WeekFields.of(Locale.KOREA);

        // 2. 나의 '연도'와 '주차 번호'를 가져옵니다.
        int myYear = this.purchaseDate.getYear();
        int myWeekNumber = this.purchaseDate.get(weekRule.weekOfWeekBasedYear());

        // 3. 비교할 날짜의 '연도'와 '주차 번호'를 가져옵니다.
        int compareYear = dateToCompare.getYear();
        int compareWeekNumber = dateToCompare.get(weekRule.weekOfWeekBasedYear());

        // 4. 두 개의 '연도'와 '주차 번호'가 "둘 다" 같은지 확인합니다.
        return (myYear == compareYear) && (myWeekNumber == compareWeekNumber);
    }

    public Lotto getPurchasedLotto() {return purchasedLotto;}
    public LocalDate getPurchaseDate() {return purchaseDate;}
    public Long getId() {return id;}
}

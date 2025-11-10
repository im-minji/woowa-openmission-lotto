package com.woowa.lotto.domain;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class WinningLotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 당첨 번호를 가지는 객체 (6개 번호 + 1개의 보너스 번호)
    @OneToOne(cascade = CascadeType.ALL)
    private final Lotto winningLotto;

    @Column
    private final Integer bonusNum;

    @Column
    private final LocalDate drawDate;

    protected WinningLotto() {
        this.winningLotto = null;
        this.bonusNum = null;
        this.drawDate = null;
    }

    public WinningLotto(Lotto winningLotto, Integer bonusNum, LocalDate drawDate) {
        validate(winningLotto, bonusNum);
        this.winningLotto = winningLotto;
        this.bonusNum = bonusNum;
        this.drawDate = drawDate;
    }

    // 보너스 번호를 가지는 객체이므로 보너스 번호에 대한 검증은 winningLotto가 해야함
    private void validate(Lotto winningLotto, Integer bonusNum) {
        Objects.requireNonNull(bonusNum, "[ERROR] 보너스 번호는 null일 수 없습니다.");

        if(bonusNum < 1 || bonusNum > 45) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호의 범위는 1~45여야 합니다.");
        }

        if(winningLotto.hasBonusNum(bonusNum)) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호는 당첨 번호와 중복되면 안됩니다.");
        }
    }

    // 서비스에서 보너스 번호, 당첨번호, 만든 날짜를 사용하기 위한 getter
    public Lotto getWinningLotto() {return winningLotto;}

    public Integer getBonusNum() {return bonusNum;}

    public LocalDate getDrawDate() {return drawDate;}

    public Long getId() {return id;}

}

package com.woowa.lotto.domain;

import java.time.LocalDate;

public class WinningLotto {
    // 당첨 번호를 가지는 객체 (6개 번호 + 1개의 보너스 번호)
    private final Lotto winningLotto;
    private final int bonusNum;
    private final LocalDate drawDate;

    public WinningLotto(Lotto winningLotto, int bonusNum, LocalDate drawDate) {
        validate(winningLotto, bonusNum);
        this.winningLotto = winningLotto;
        this.bonusNum = bonusNum;
        this.drawDate = drawDate;
    }

    // 보너스 번호를 가지는 객체이므로 보너스 번호에 대한 검증은 winningLotto가 해야함
    private void validate(Lotto winningLotto, int bonusNum) {
        if(bonusNum < 1 || bonusNum > 45) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호의 범위는 1~45여야 합니다.");
        }

        if(winningLotto.hasBonusNum(bonusNum)) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호는 당첨 번호와 중복되면 안됩니다.");
        }
    }

    // 서비스에서 보너스 번호, 당첨번호, 만든 날짜를 사용하기 위한 getter
    public Lotto getWinningLotto() {return winningLotto;}

    public int getBonusNum() {return bonusNum;}

    public LocalDate getDrawDate() {return drawDate;}

}

package com.woowa.lotto.domain;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
public class MyLotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    // [제거] @JoinColumn은 @Embedded와 함께 사용되지 않으므로 제거
    private final Lotto myLotto;

    @Column
    private final String lottoName;

    protected MyLotto() {
        this.myLotto = null;
        this.lottoName = null;
    }

    public MyLotto(Lotto myLotto, String lottoName) {
        this.myLotto = Objects.requireNonNull(myLotto, "[ERROR] 로또가 비워져 있습니다.");

        Objects.requireNonNull(lottoName, "[ERROR] 로또 이름은 null일 수 없습니다.");

        validateLottoName(lottoName);
        this.lottoName = lottoName;
    }

    private void validateLottoName(String lottoName) {
        if(lottoName.length() > 20 || lottoName.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 로또 이름은 1~20글자 범위로 입력해주세요.");
        }
    }

    public Lotto getMyLotto() {return myLotto;}

    public String getLottoName() {return lottoName;}

    public Long getId() {return id;}
}


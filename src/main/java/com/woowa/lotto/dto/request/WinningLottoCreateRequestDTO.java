package com.woowa.lotto.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WinningLottoCreateRequestDTO {
    // 당첨 번호 6개
    private List<Integer> numbers;

    // 보너스 번호 1개
    private Integer bonusNum;

    // 당첨 번호 날짜
    private LocalDate drawDate;
}
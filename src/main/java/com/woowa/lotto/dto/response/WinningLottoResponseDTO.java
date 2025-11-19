package com.woowa.lotto.dto.response;

import com.woowa.lotto.domain.WinningLotto;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class WinningLottoResponseDTO {
    private Long id;
    private LocalDate drawDate;
    private List<Integer> numbers;
    private Integer bonusNum;

    public static WinningLottoResponseDTO from(WinningLotto entity) {
        return WinningLottoResponseDTO.builder()
                .id(entity.getId())
                .drawDate(entity.getDrawDate())
                .numbers(entity.getWinningLotto().getNumbers())
                .bonusNum(entity.getBonusNum())
                .build();
    }
}
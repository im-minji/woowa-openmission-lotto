package com.woowa.lotto.dto.response;

import com.woowa.lotto.domain.LottoRank;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class LottoResultResponseDTO {
    private Long id;                // 로또 ID
    private LocalDate purchaseDate; // 구매 날짜
    private List<Integer> numbers;  // 로또 번호
    private LottoRank rank;         // 당첨 등수
}
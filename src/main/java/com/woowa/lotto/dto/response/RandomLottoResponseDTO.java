package com.woowa.lotto.dto.response;

import com.woowa.lotto.domain.Lotto;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * '랜덤 로또' 생성 응답(Response) DTO (DB 저장 X)
 * Controller -> 클라이언트
 */
@Getter
@Builder // Service에서 DTO를 생성할 때 .build() 패턴을 사용
public class RandomLottoResponseDTO {

    private final List<Integer> numbers;

    // Service가 Lotto(값 객체)를 DTO로 변환할 때 사용하는 팩토리 메서드
    public static RandomLottoResponseDTO from(Lotto lotto) {
        return RandomLottoResponseDTO.builder()
                .numbers(lotto.getNumbers())
                .build();
    }
}
package com.woowa.lotto.dto.response;

import com.woowa.lotto.domain.MyLotto;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * '나만의 로또' 저장 결과 응답(Response) DTO (DB 저장 O)
 * Controller -> 클라이언트
 */
@Getter
@Builder // Service에서 DTO를 생성할 때 .build() 패턴을 사용
public class MyLottoResponseDTO {

    private final Long id;
    private final String lottoName;
    private final List<Integer> numbers;

    // Service가 MyLotto(엔티티)를 DTO로 변환할 때 사용하는 팩토리 메서드
    public static MyLottoResponseDTO from(MyLotto entity) {
        return MyLottoResponseDTO.builder()
                .id(entity.getId())
                .lottoName(entity.getLottoName())
                .numbers(entity.getMyLotto().getNumbers())
                .build();
    }
}
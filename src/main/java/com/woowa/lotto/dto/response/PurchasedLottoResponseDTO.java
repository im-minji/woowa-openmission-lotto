package com.woowa.lotto.dto.response;

import com.woowa.lotto.domain.PurchasedLotto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 로또 '구매' 성공 시 공통으로 반환하는 DTO (영수증)
 * (수동 구매, '나만의 로또'에서 복사 구매 시 모두 사용)
 */
@Getter
public class PurchasedLottoResponseDTO {

    private final Long id;
    private final List<Integer> numbers;
    private final LocalDate purchaseDate;

    @Builder
    public PurchasedLottoResponseDTO(Long id, List<Integer> numbers, LocalDate purchaseDate) {
        this.id = id;
        this.numbers = numbers;
        this.purchaseDate = purchaseDate;
    }

    /**
     * PurchasedLotto 엔티티를 PurchasedLottoResponseDTO로 변환
     * entity (DB에서 저장되거나 조회된 엔티티)
     * @return 변환된 DTO
     */
    public static PurchasedLottoResponseDTO from(PurchasedLotto entity) {
        return PurchasedLottoResponseDTO.builder()
                .id(entity.getId())
                .numbers(entity.getPurchasedLotto().getNumbers()) // @Embedded된 Lotto 객체에서 번호 가져오기
                .purchaseDate(entity.getPurchaseDate())
                .build();
    }
}
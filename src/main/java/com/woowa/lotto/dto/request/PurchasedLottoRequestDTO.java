package com.woowa.lotto.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// '수동' 번호로 로또를 '구매'할 때 사용하는 DTO (주문서)
@Getter
@Setter
@NoArgsConstructor // JSON 역직렬화를 위해 기본 생성자 필요
public class PurchasedLottoRequestDTO {
    private List<Integer> numbers;
}
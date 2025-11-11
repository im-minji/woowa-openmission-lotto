package com.woowa.lotto.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * '나만의 로또' 생성 요청(Request) DTO
 * 클라이언트(Postman의 Body) -> Controller
 */
@Getter
@Setter // Controller에서 JSON을 객체로 바인딩(deserialization)하기 위해 필요
@NoArgsConstructor // JSON 바인딩을 위한 기본 생성자
public class MyLottoRequestDTO {

    private List<Integer> numbers;
    private String lottoName;

    // 이 DTO는 클라이언트로부터 데이터를 받기만 하는 역할
    // toEntity() 같은 로직은 Service 계층에서 DTO를 읽어서 직접 처리
}
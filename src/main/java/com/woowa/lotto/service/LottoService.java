package com.woowa.lotto.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.woowa.lotto.utils.Randoms;
import com.woowa.lotto.domain.*;

import com.woowa.lotto.dto.response.RandomLottoResponseDTO;
import com.woowa.lotto.dto.request.MyLottoRequestDTO;
import com.woowa.lotto.dto.response.MyLottoResponseDTO;

import com.woowa.lotto.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;

@RequiredArgsConstructor
@Transactional
@Service
public class LottoService {
    private final MyLottoRepository myLottoRepository;
    private final PurchasedLottoRepository purchasedLottoRepository;
    private final WinningLottoRepository winningLottoRepository;

    /**
     * 1. 로또 번호 생성 (Generate)
     * (DB 저장 X)
     */
    @Transactional(readOnly = true)
    // [수정] 반환 타입 LottoResponseDTO -> RandomLottoResponseDTO
    public RandomLottoResponseDTO generateRandomLotto() {
        List<Integer> numbers = Randoms.pickUniqueNumbersInRange(1, 45, 6);
        Lotto lotto = new Lotto(numbers); // Lotto 값 객체 생성 (검증 포함)

        // Entity(Lotto) 대신 DTO(RandomLottoResponseDTO)를 반환합니다.
        return RandomLottoResponseDTO.from(lotto);
    }

    /**
     * 2. 나만의 번호 리스트에 저장 (Favorites)
     * (랜덤/수동 공통 로직)
     * request (lottoName, numbers)가 들어있는 DTO
     * @return 저장된 결과 (id, lottoName, numbers) DTO
     */
    public MyLottoResponseDTO saveToMyLotto(MyLottoRequestDTO request) {
        // DTO의 데이터를 사용
        Lotto lotto = new Lotto(request.getNumbers());
        MyLotto myLotto = new MyLotto(lotto, request.getLottoName());

        MyLotto savedEntity = myLottoRepository.save(myLotto);

        // 컨트롤러(클라이언트)에는 'DTO'로 변환하여 반환
        return MyLottoResponseDTO.from(savedEntity);
    }

    // TODO: 3. 구매 번호 리스트 (Purchases) 기능 구현
    // TODO: 4. 당첨 결과 및 통계 (Statistics) 기능 구현
}

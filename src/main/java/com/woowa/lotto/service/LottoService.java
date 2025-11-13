package com.woowa.lotto.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.woowa.lotto.utils.Randoms;
import com.woowa.lotto.domain.*;

import com.woowa.lotto.dto.response.RandomLottoResponseDTO;
import com.woowa.lotto.dto.request.MyLottoRequestDTO;
import com.woowa.lotto.dto.response.MyLottoResponseDTO;
import com.woowa.lotto.dto.response.PurchasedLottoResponseDTO;
import com.woowa.lotto.dto.request.PurchasedLottoRequestDTO;

import com.woowa.lotto.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

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
    public RandomLottoResponseDTO generateRandomLotto() {
        List<Integer> numbers = Randoms.pickUniqueNumbersInRange(1, 45, 6);
        Lotto lotto = new Lotto(numbers); // Lotto 값 객체 생성 (검증 포함)
        // Entity(Lotto) 대신 DTO(RandomLottoResponseDTO)를 반환
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

    // 나만의 로또 리스트 전체 조회
    @Transactional(readOnly = true)
    public List<MyLottoResponseDTO> findAllMyLotto() {
        return myLottoRepository.findAll().stream()
                // ID(생성순) 오름차순 정렬
                .sorted(Comparator.comparing(MyLotto::getId))
                .map(MyLottoResponseDTO::from) // DTO로 변환
                .collect(Collectors.toList());
    }

    // 나만의 로또 리스트에서 로또 삭제
    public void deleteMyLotto(Long myLottoId) {
        // 삭제 전, 존재하는 ID인지 확인 (더 안전한 로직)
        if (!myLottoRepository.existsById(myLottoId)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 '나만의 로또' ID입니다: " + myLottoId);
        }
        myLottoRepository.deleteById(myLottoId);
    }

    // TODO: 3. 구매 번호 리스트 (Purchases) 기능

    //[Service] '수동' 번호로 로또 구매
    public PurchasedLottoResponseDTO purchaseManualLotto(PurchasedLottoRequestDTO request) {
        Lotto lotto = new Lotto(request.getNumbers());
        // 공통 구매 로직 호출
        return purchaseLottoInternal(lotto);
    }

    // [Service] '나만의 로또'에서 '복사'하여 로또 구매
    public PurchasedLottoResponseDTO purchaseFromMyLotto(Long myLottoId) {
        // 1. '나만의 로또'를 DB에서 찾는다. (없으면 예외 발생)
        MyLotto myLotto = myLottoRepository.findById(myLottoId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 '나만의 로또' ID입니다: " + myLottoId));

        // 2. '나만의 로또'에서 Lotto (값 객체)를 '읽어온다(복사)'
        Lotto lottoToCopy = myLotto.getMyLotto();

        // 3. 공통 구매 로직 호출
        return purchaseLottoInternal(lottoToCopy);
    }


    /**
     * 로또 구매 공통 로직
     * - 어떤 방식(수동/복사)이든, 'Lotto' 객체를 받아서
     * - '오늘 날짜'를 찍고 'PurchasedLotto' 엔티티로 저장
     */
    private PurchasedLottoResponseDTO purchaseLottoInternal(Lotto lotto) {
        // 1. '구매 날짜'는 서비스에서 오늘 날짜로 생성
        LocalDate today = LocalDate.now();

        // 2. '구매한 로또' 엔티티 생성
        PurchasedLotto purchasedLotto = new PurchasedLotto(lotto, today);

        // 3. DB에 저장
        PurchasedLotto savedLotto = purchasedLottoRepository.save(purchasedLotto);

        // 4. "영수증" DTO로 변환하여 반환
        return PurchasedLottoResponseDTO.from(savedLotto);
    }

    // 구매 로또 조회
    @Transactional(readOnly = true)
    public List<PurchasedLottoResponseDTO> findAllPurchasedLotto() {
        return purchasedLottoRepository.findAll().stream()
                // 구매일(purchaseDate) 최신순(내림차순) 정렬
                .sorted(Comparator.comparing(PurchasedLotto::getPurchaseDate).reversed())
                .map(PurchasedLottoResponseDTO::from) // DTO로 변환
                .collect(Collectors.toList());
    }


    // 구매 로또 삭제
    public void deletePurchasedLotto(Long purchasedLottoId) {
        // 삭제 전, 존재하는 ID인지 확인
        if (!purchasedLottoRepository.existsById(purchasedLottoId)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 '구매한 로또' ID입니다: " + purchasedLottoId);
        }
        purchasedLottoRepository.deleteById(purchasedLottoId);
    }
    // TODO: 4. 당첨 결과 및 통계 (Statistics) 기능 구현
}

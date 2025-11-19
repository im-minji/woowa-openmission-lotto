package com.woowa.lotto.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.woowa.lotto.utils.Randoms;
import com.woowa.lotto.domain.*;

import com.woowa.lotto.dto.response.LottoResultResponseDTO;
import com.woowa.lotto.dto.response.WinningLottoResponseDTO;
import com.woowa.lotto.dto.response.RandomLottoResponseDTO;
import com.woowa.lotto.dto.request.MyLottoRequestDTO;
import com.woowa.lotto.dto.response.MyLottoResponseDTO;
import com.woowa.lotto.dto.response.PurchasedLottoResponseDTO;
import com.woowa.lotto.dto.request.PurchasedLottoRequestDTO;
import com.woowa.lotto.dto.response.StatisticsResponseDTO;
import com.woowa.lotto.dto.request.WinningLottoCreateRequestDTO;

import com.woowa.lotto.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

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

    // --- 3. 구매 번호 리스트 (Purchases) 기능 ---

    // [Service] '수동' 번호로 로또 구매 (날짜 선택 가능)
    // [수정] 중복되었던 메서드 중 '최신 버전'만 남김
    public PurchasedLottoResponseDTO purchaseManualLotto(PurchasedLottoRequestDTO request) {
        Lotto lotto = new Lotto(request.getNumbers());
        // DTO에서 날짜를 꺼내서 공통 로직으로 전달
        return purchaseLottoInternal(lotto, request.getPurchaseDate());
    }

    // [Service] '나만의 로또'에서 '복사'하여 로또 구매 (오늘 날짜로 구매)
    public PurchasedLottoResponseDTO purchaseFromMyLotto(Long myLottoId) {
        // 1. '나만의 로또'를 DB에서 찾는다. (없으면 예외 발생)
        MyLotto myLotto = myLottoRepository.findById(myLottoId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 '나만의 로또' ID입니다: " + myLottoId));

        // 2. Lotto 객체를 공유하지 않고, 번호만 읽어오기
        List<Integer> numbersToCopy = myLotto.getMyLotto().getNumbers();

        // 3. 그 번호로 새로운 Lotto 값 객체를 생성 (복사)
        Lotto newLotto = new Lotto(numbersToCopy);

        // 4. [수정] '오늘 날짜'와 함께 새로운 공통 로직 호출
        return purchaseLottoInternal(newLotto, LocalDate.now());
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


    private PurchasedLottoResponseDTO purchaseLottoInternal(Lotto lotto, LocalDate purchaseDate) {
        // DTO에서 받은 날짜가 null이 아닌지 확인
        Objects.requireNonNull(purchaseDate, "[ERROR] 구매 날짜는 null일 수 없습니다.");

        // DTO에서 받은 날짜를 사용
        PurchasedLotto purchasedLotto = new PurchasedLotto(lotto, purchaseDate);
        PurchasedLotto savedLotto = purchasedLottoRepository.save(purchasedLotto);
        return PurchasedLottoResponseDTO.from(savedLotto);
    }

    // TODO: 4. 당첨 결과 및 통계 (Statistics) 기능 구현

    public void createWinningLotto(WinningLottoCreateRequestDTO request) {
        Lotto lotto = new Lotto(request.getNumbers());
        WinningLotto winningLotto = new WinningLotto(
                lotto,
                request.getBonusNum(),
                request.getDrawDate()
        );
        winningLottoRepository.save(winningLotto);
    }

    @Transactional(readOnly = true)
    public List<WinningLottoResponseDTO> findAllWinningLottos() {
        return winningLottoRepository.findAll().stream()
                // 추첨일 내림차순 정렬 (최신 날짜가 위로)
                .sorted((a, b) -> b.getDrawDate().compareTo(a.getDrawDate()))
                .map(WinningLottoResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StatisticsResponseDTO getStatistics(LocalDate queryDate) {
        // 주간 범위 계산
        LocalDate startOfWeek = queryDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = queryDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 당첨 번호 조회
        WinningLotto winningLotto = winningLottoRepository.findByDrawDateBetween(startOfWeek, endOfWeek)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("[ERROR] 해당 주차(%s ~ %s)의 당첨 번호가 존재하지 않습니다.", startOfWeek, endOfWeek)));

        // 구매 목록 조회
        List<PurchasedLotto> purchasedLottos = purchasedLottoRepository.findAllByPurchaseDateBetween(startOfWeek, endOfWeek);

        // 통계 계산 및 상세 결과 리스트 생성
        List<LottoResultResponseDTO> lottoResults = new ArrayList<>();
        Map<LottoRank, Integer> rankCounts = new EnumMap<>(LottoRank.class);

        for (LottoRank rank : LottoRank.values()) {
            rankCounts.put(rank, 0);
        }

        long totalWinningMoney = 0; // 총 당첨 금액

        for (PurchasedLotto ticket : purchasedLottos) {
            // 등수 판별
            LottoRank rank = checkRank(ticket, winningLotto);

            // 통계 누적
            rankCounts.put(rank, rankCounts.get(rank) + 1);
            totalWinningMoney += rank.getWinningPrize();

            // 상세 결과 DTO 생성 및 추가
            lottoResults.add(LottoResultResponseDTO.builder()
                    .id(ticket.getId())
                    .purchaseDate(ticket.getPurchaseDate())
                    .numbers(ticket.getPurchasedLotto().getNumbers())
                    .rank(rank)
                    .build());
        }

        // 수익률 계산
        long totalPurchaseAmount = purchasedLottos.size() * 1000L;
        double rateOfReturn = 0.0;
        if (totalPurchaseAmount > 0) {
            rateOfReturn = ((double) totalWinningMoney / totalPurchaseAmount) * 100.0;
        }

        // DTO 반환
        return StatisticsResponseDTO.builder()
                .drawDate(winningLotto.getDrawDate())
                .winningNumbers(winningLotto.getWinningLotto().getNumbers())
                .bonusNum(winningLotto.getBonusNum())
                .rankCounts(rankCounts)
                .rateOfReturn(rateOfReturn)
                .totalPurchaseAmount(totalPurchaseAmount)
                .totalWinningMoney(totalWinningMoney)
                .lottoResults(lottoResults)
                .build();
    }

    // 등수 판별
    private LottoRank checkRank(PurchasedLotto ticket, WinningLotto winningLotto) {
        Lotto userLotto = ticket.getPurchasedLotto();
        Lotto winningNumbers = winningLotto.getWinningLotto();

        int matchCount = userLotto.matchCount(winningNumbers);
        boolean hasBonus = userLotto.hasBonusNum(winningLotto.getBonusNum());

        return LottoRank.find(matchCount, hasBonus);
    }
}
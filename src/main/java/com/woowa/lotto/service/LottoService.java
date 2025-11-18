package com.woowa.lotto.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    // 당첨 번호 및 보너스 번호 등록
    public void createWinningLotto(WinningLottoCreateRequestDTO request) {
        // Lotto 객체 생성 (유효성 검증 자동 수행)
        Lotto lotto = new Lotto(request.getNumbers());

        // WinningLotto 엔티티 생성 (보너스 번호 검증 수행)
        WinningLotto winningLotto = new WinningLotto(
                lotto,
                request.getBonusNum(),
                request.getDrawDate()
        );

        winningLottoRepository.save(winningLotto);
    }

    // 입력받은 날짜가 포함된 '주(Week)'의 구매 내역과 당첨 결과를 비교하여 통계를 반환
    @Transactional(readOnly = true)
    public StatisticsResponseDTO getStatistics(LocalDate queryDate) {
        // 주간 범위 계산 (월요일 ~ 일요일)
        LocalDate startOfWeek = queryDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = queryDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 2. 해당 주차의 '당첨 번호' 조회
        WinningLotto winningLotto = winningLottoRepository.findByDrawDateBetween(startOfWeek, endOfWeek)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("[ERROR] 해당 주차(%s ~ %s)의 당첨 번호가 입력되지 않았습니다.", startOfWeek, endOfWeek)));

        // 해당 주차에 '구매한 로또' 목록 조회
        List<PurchasedLotto> purchasedLottos = purchasedLottoRepository.findAllByPurchaseDateBetween(startOfWeek, endOfWeek);

        // 통계 계산 (Lotto 도메인 메서드 활용)
        Map<LottoRank, Integer> statisticsMap = calculateRankStatistics(purchasedLottos, winningLotto);
        LottoResult lottoResult = new LottoResult(statisticsMap);

        // 수익률 계산
        int purchaseAmount = purchasedLottos.size() * 1000;
        double rateOfReturn = lottoResult.getRateOfReturn(purchaseAmount);

        // DTO 반환
        return StatisticsResponseDTO.builder()
                .drawDate(winningLotto.getDrawDate())
                .winningNumbers(winningLotto.getWinningLotto().getNumbers())
                .bonusNum(winningLotto.getBonusNum())
                .rankCounts(lottoResult.getStatistics())
                .rateOfReturn(rateOfReturn)
                .build();
    }


    private Map<LottoRank, Integer> calculateRankStatistics(List<PurchasedLotto> tickets, WinningLotto winningLotto) {
        // 빈 통계 맵 초기화
        Map<LottoRank, Integer> stats = new EnumMap<>(LottoRank.class);
        for (LottoRank rank : LottoRank.values()) {
            stats.put(rank, 0);
        }

        // 당첨 번호 로또 객체 가져오기
        Lotto winningNumbers = winningLotto.getWinningLotto();
        int bonusNumber = winningLotto.getBonusNum();

        for (PurchasedLotto ticket : tickets) {
            Lotto userLotto = ticket.getPurchasedLotto(); // 사용자 로또

            int matchCount = userLotto.matchCount(winningNumbers);
            boolean hasBonus = userLotto.hasBonusNum(bonusNumber);

            LottoRank rank = LottoRank.find(matchCount, hasBonus);
            stats.put(rank, stats.get(rank) + 1);
        }

        return stats;
    }
}
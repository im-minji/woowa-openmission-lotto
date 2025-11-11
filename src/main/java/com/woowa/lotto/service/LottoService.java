package com.woowa.lotto.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.woowa.lotto.utils.Randoms;
import com.woowa.lotto.domain.*;
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
    private final LottoRepository lottoRepository;
    private final MyLottoRepository myLottoRepository;
    private final PurchasedLottoRepository purchasedLottoRepository;
    private final WinningLottoRepository winningLottoRepository;

    @Transactional(readOnly = true)
    public Lotto generateRandomLotto() {
        List<Integer> numbers = Randoms.pickUniqueNumbersInRange(1, 45, 6);
        return new Lotto(numbers);
    }

    /**
     * 나만의 로또를 생성하고 DB에 저장
     * numbers 로또 번호 6개
     * name    로또 이름
     * @return 저장된 MyLotto 엔티티 (ID가 발급됨)
     */
    // 랜덤 번호 저장이나 내가 수동으로 저장하는 거나 상관없이 재사용되는 로직
    public MyLotto saveToMyLotto(List<Integer> numbers, String name) {
        Lotto lotto = new Lotto(numbers);
        MyLotto myLotto = new MyLotto(lotto, name);
        return myLottoRepository.save(myLotto);
    }
}

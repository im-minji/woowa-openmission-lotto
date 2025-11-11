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
}

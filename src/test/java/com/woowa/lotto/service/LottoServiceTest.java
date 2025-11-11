package com.woowa.lotto.service;


import com.woowa.lotto.domain.Lotto;
import com.woowa.lotto.domain.MyLotto;
import com.woowa.lotto.dto.request.MyLottoRequestDTO;
import com.woowa.lotto.dto.response.MyLottoResponseDTO;
import com.woowa.lotto.dto.response.RandomLottoResponseDTO;
import com.woowa.lotto.repository.MyLottoRepository;
import com.woowa.lotto.repository.PurchasedLottoRepository;
import com.woowa.lotto.repository.WinningLottoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class LottoServiceTest {

    @Mock
    private MyLottoRepository myLottoRepository;

    @Mock
    private PurchasedLottoRepository purchasedLottoRepository;

    @Mock
    private WinningLottoRepository winningLottoRepository;

    @InjectMocks
    private LottoService lottoService;

    @Test
    @DisplayName("1. [Service] 랜덤 로또 생성 시 6개의 숫자를 가진 DTO를 반환한다")
    void generateRandomLotto_ShouldReturnDTO_With6Numbers() {
        RandomLottoResponseDTO response = lottoService.generateRandomLotto();

        // Lotto 생성자에서 이미 유효성 검사(6개, 중복X, 범위 1-45)를 수행
        // 서비스는 이 로직이 성공적으로 수행되고 DTO로 잘 변환되었는지만 확인
        assertThat(response).isNotNull();
        assertThat(response.getNumbers()).isNotNull();
        assertThat(response.getNumbers()).hasSize(6);
        assertThat(response.getNumbers()).doesNotHaveDuplicates(); // 중복 없는지 확인
    }

    @Test
    @DisplayName("2. [Service] '나만의 로또' 저장 요청 시, 엔티티를 DB에 저장하고 응답 DTO를 반환한다")
    void saveToMyLotto_ShouldSaveEntity_AndReturnResponseDTO() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        String lottoName = "내 첫 로또";
        MyLottoRequestDTO request = new MyLottoRequestDTO();
        request.setNumbers(numbers);
        request.setLottoName(lottoName);

        MyLotto savedEntityMock = mock(MyLotto.class);
        given(savedEntityMock.getId()).willReturn(1L);
        given(savedEntityMock.getLottoName()).willReturn(lottoName);
        given(savedEntityMock.getMyLotto()).willReturn(new Lotto(numbers));

        given(myLottoRepository.save(any(MyLotto.class))).willReturn(savedEntityMock);

        MyLottoResponseDTO response = lottoService.saveToMyLotto(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getLottoName()).isEqualTo(lottoName);
        assertThat(response.getNumbers()).containsExactly(1, 2, 3, 4, 5, 6);

        verify(myLottoRepository, times(1)).save(any(MyLotto.class));

        ArgumentCaptor<MyLotto> captor = ArgumentCaptor.forClass(MyLotto.class);
        verify(myLottoRepository).save(captor.capture());

        MyLotto entityPassedToSave = captor.getValue();
        assertThat(entityPassedToSave.getLottoName()).isEqualTo(lottoName);
        assertThat(entityPassedToSave.getMyLotto().getNumbers()).containsExactly(1, 2, 3, 4, 5, 6);
        assertThat(entityPassedToSave.getId()).isNull();
    }
}
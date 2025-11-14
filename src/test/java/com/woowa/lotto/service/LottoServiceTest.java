package com.woowa.lotto.service;

import com.woowa.lotto.domain.Lotto;
import com.woowa.lotto.domain.MyLotto;
import com.woowa.lotto.domain.PurchasedLotto;
import com.woowa.lotto.dto.request.MyLottoRequestDTO;
import com.woowa.lotto.dto.request.PurchasedLottoRequestDTO;
import com.woowa.lotto.dto.response.MyLottoResponseDTO;
import com.woowa.lotto.dto.response.PurchasedLottoResponseDTO;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
    @DisplayName("랜덤 로또 생성 시 6개의 숫자를 가진 DTO를 반환한다")
    void generateRandomLotto_ShouldReturnDTO_With6Numbers() {
        RandomLottoResponseDTO response = lottoService.generateRandomLotto();

        assertThat(response).isNotNull();
        assertThat(response.getNumbers()).isNotNull();
        assertThat(response.getNumbers()).hasSize(6);
        assertThat(response.getNumbers()).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("MyLotto 저장 요청 시, 엔티티를 DB에 저장하고 응답 DTO를 반환한다")
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
    }

    @Test
    @DisplayName("MyLotto 전체 조회 시, ID 오름차순으로 정렬된 DTO 리스트를 반환한다")
    void findAllMyLotto_ShouldReturnSortedDTOList() {
        MyLotto lotto2 = mock(MyLotto.class);
        given(lotto2.getId()).willReturn(2L);
        given(lotto2.getLottoName()).willReturn("두번째");
        given(lotto2.getMyLotto()).willReturn(new Lotto(List.of(1, 2, 3, 4, 5, 6)));

        MyLotto lotto1 = mock(MyLotto.class);
        given(lotto1.getId()).willReturn(1L);
        given(lotto1.getLottoName()).willReturn("첫번째");
        given(lotto1.getMyLotto()).willReturn(new Lotto(List.of(7, 8, 9, 10, 11, 12)));

        given(myLottoRepository.findAll()).willReturn(List.of(lotto2, lotto1));

        List<MyLottoResponseDTO> responseList = lottoService.findAllMyLotto();

        assertThat(responseList).isNotNull();
        assertThat(responseList).hasSize(2);
        assertThat(responseList.get(0).getId()).isEqualTo(1L);
        assertThat(responseList.get(1).getId()).isEqualTo(2L);
        assertThat(responseList.get(0).getLottoName()).isEqualTo("첫번째");
    }

    @Test
    @DisplayName("MyLotto 삭제 요청 시, 'existsById'와 'deleteById'를 호출한다")
    void deleteMyLotto_ShouldCallExistsAndDelete() {
        Long lottoId = 1L;
        given(myLottoRepository.existsById(lottoId)).willReturn(true);

        lottoService.deleteMyLotto(lottoId);

        verify(myLottoRepository, times(1)).existsById(lottoId);
        verify(myLottoRepository, times(1)).deleteById(lottoId);
    }

    @Test
    @DisplayName("존재하지 않는 MyLotto 삭제 요청 시 예외를 발생시킨다")
    void deleteMyLotto_ShouldThrowException_WhenNotFound() {
        Long notFoundId = 99L;
        given(myLottoRepository.existsById(notFoundId)).willReturn(false);

        assertThatThrownBy(() -> lottoService.deleteMyLotto(notFoundId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 존재하지 않는 '나만의 로또' ID입니다");

        verify(myLottoRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("수동 구매 요청 시, 오늘 날짜로 DB에 저장하고 응답 DTO를 반환한다")
    void purchaseManualLotto_ShouldSaveWithTodayDate_AndReturnDTO() {
        List<Integer> numbers = List.of(7, 8, 9, 10, 11, 12);
        PurchasedLottoRequestDTO request = new PurchasedLottoRequestDTO();
        request.setNumbers(numbers);

        PurchasedLotto savedEntityMock = mock(PurchasedLotto.class);
        given(savedEntityMock.getId()).willReturn(1L);
        given(savedEntityMock.getPurchaseDate()).willReturn(LocalDate.now());
        given(savedEntityMock.getPurchasedLotto()).willReturn(new Lotto(numbers));

        given(purchasedLottoRepository.save(any(PurchasedLotto.class))).willReturn(savedEntityMock);

        PurchasedLottoResponseDTO response = lottoService.purchaseManualLotto(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPurchaseDate()).isEqualTo(LocalDate.now());
        assertThat(response.getNumbers()).containsExactly(7, 8, 9, 10, 11, 12);

        ArgumentCaptor<PurchasedLotto> captor = ArgumentCaptor.forClass(PurchasedLotto.class);
        verify(purchasedLottoRepository, times(1)).save(captor.capture());

        PurchasedLotto entityPassedToSave = captor.getValue();
        assertThat(entityPassedToSave.getId()).isNull();
        assertThat(entityPassedToSave.getPurchaseDate()).isEqualTo(LocalDate.now());
        assertThat(entityPassedToSave.getPurchasedLotto().getNumbers()).isEqualTo(numbers);
    }

    @Test
    @DisplayName("MyLotto를 구매 요청 시, 번호를 복사하여 DB에 저장하고 DTO를 반환한다")
    void purchaseFromMyLotto_ShouldCopyNumbersAndSave_AndReturnDTO() {
        Long myLottoId = 5L;
        List<Integer> copiedNumbers = List.of(1, 2, 3, 4, 5, 6);

        MyLotto myLottoMock = mock(MyLotto.class);
        given(myLottoMock.getMyLotto()).willReturn(new Lotto(copiedNumbers));
        given(myLottoRepository.findById(myLottoId)).willReturn(Optional.of(myLottoMock));

        PurchasedLotto savedEntityMock = mock(PurchasedLotto.class);
        given(savedEntityMock.getId()).willReturn(1L);
        given(savedEntityMock.getPurchaseDate()).willReturn(LocalDate.now());
        given(savedEntityMock.getPurchasedLotto()).willReturn(new Lotto(copiedNumbers));

        given(purchasedLottoRepository.save(any(PurchasedLotto.class))).willReturn(savedEntityMock);

        PurchasedLottoResponseDTO response = lottoService.purchaseFromMyLotto(myLottoId);

        verify(myLottoRepository, times(1)).findById(myLottoId);
        verify(purchasedLottoRepository, times(1)).save(any(PurchasedLotto.class));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNumbers()).isEqualTo(copiedNumbers);
    }

    @Test
    @DisplayName("PurchasedLotto 전체 조회 시 구매일(최신순) 정렬된 DTO 리스트를 반환한다")
    void findAllPurchasedLotto_ShouldReturnSortedByDateDescDTOList() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();

        PurchasedLotto lottoToday = mock(PurchasedLotto.class);
        given(lottoToday.getId()).willReturn(2L);
        given(lottoToday.getPurchaseDate()).willReturn(today);
        given(lottoToday.getPurchasedLotto()).willReturn(new Lotto(List.of(1, 2, 3, 4, 5, 6)));

        PurchasedLotto lottoYesterday = mock(PurchasedLotto.class);
        given(lottoYesterday.getId()).willReturn(1L);
        given(lottoYesterday.getPurchaseDate()).willReturn(yesterday);
        given(lottoYesterday.getPurchasedLotto()).willReturn(new Lotto(List.of(7, 8, 9, 10, 11, 12)));

        given(purchasedLottoRepository.findAll()).willReturn(List.of(lottoYesterday, lottoToday));

        List<PurchasedLottoResponseDTO> responseList = lottoService.findAllPurchasedLotto();

        assertThat(responseList).hasSize(2);
        assertThat(responseList.get(0).getId()).isEqualTo(2L);
        assertThat(responseList.get(1).getId()).isEqualTo(1L);
        assertThat(responseList.get(0).getPurchaseDate()).isEqualTo(today);
    }

    @Test
    @DisplayName("PurchasedLotto 삭제 요청 시 'existsById'와 'deleteById'를 호출한다")
    void deletePurchasedLotto_ShouldCallExistsAndDelete() {
        Long lottoId = 1L;
        given(purchasedLottoRepository.existsById(lottoId)).willReturn(true);

        lottoService.deletePurchasedLotto(lottoId);

        verify(purchasedLottoRepository, times(1)).existsById(lottoId);
        verify(purchasedLottoRepository, times(1)).deleteById(lottoId);
    }
}
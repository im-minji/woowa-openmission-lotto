package com.woowa.lotto.controller;

import com.woowa.lotto.dto.request.MyLottoRequestDTO;
import com.woowa.lotto.dto.request.PurchasedLottoRequestDTO;
import com.woowa.lotto.dto.response.MyLottoResponseDTO;
import com.woowa.lotto.dto.response.PurchasedLottoResponseDTO;
import com.woowa.lotto.dto.response.RandomLottoResponseDTO;
import com.woowa.lotto.service.LottoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/im-minji")

@RequiredArgsConstructor
public class LottoController {
    private final LottoService lottoService;

    // MyLotto
    @GetMapping("/lotto/random")
    public RandomLottoResponseDTO getRandomLotto() {
        return lottoService.generateRandomLotto();
    }

    @PostMapping("/my-lotto")
    public MyLottoResponseDTO saveMyLotto(@RequestBody MyLottoRequestDTO saveMyLottoRequest) {
        return lottoService.saveToMyLotto(saveMyLottoRequest);
    }

    @GetMapping("/my-lotto")
    public List<MyLottoResponseDTO> readAllMyLotto() {
        return lottoService.findAllMyLotto();
    }

    @DeleteMapping("/my-lotto/{myLottoId}")
    public void deleteMyLotto(@PathVariable Long myLottoId) {
        lottoService.deleteMyLotto(myLottoId);
    }


    // PusrchasedLotto
    @PostMapping("/purchase/manual")
    public PurchasedLottoResponseDTO purchaseManualLotto(@RequestBody PurchasedLottoRequestDTO requestDTO) {
        return lottoService.purchaseManualLotto(requestDTO);
    }

    @PostMapping("/purchase/from-my-lotto/{myLottoId}")
    public PurchasedLottoResponseDTO purchasedMyLotto (@PathVariable Long myLottoId) {
        return lottoService.purchaseFromMyLotto(myLottoId);
    }

    @GetMapping("/purchase")
    public List<PurchasedLottoResponseDTO> readAllPurchasedLotto() {
        return lottoService.findAllPurchasedLotto();
    }

    @DeleteMapping("/purchase/{purchaseId}")
    public void deletePurchasedLotto(@PathVariable Long purchaseId) {
        lottoService.deletePurchasedLotto(purchaseId);
    }

}

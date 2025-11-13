package com.woowa.lotto.controller;

import com.woowa.lotto.dto.response.RandomLottoResponseDTO;
import com.woowa.lotto.service.LottoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/im-minji")

@RequiredArgsConstructor
public class LottoController {
    private final LottoService lottoService;

    @GetMapping("/lotto/random")
    public RandomLottoResponseDTO getRandomLotto() {
        return lottoService.generateRandomLotto();
    }
}

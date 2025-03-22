package org.bbsk.mysender.api.alphavantage.controller;

import org.bbsk.mysender.api.alphavantage.dto.StaticStockDto;
import org.bbsk.mysender.api.alphavantage.dto.StockDto;
import org.bbsk.mysender.api.alphavantage.scheduler.AlphaVantageScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlphaVantageController {

    private final AlphaVantageScheduler alphaVantageScheduler;

    public AlphaVantageController(AlphaVantageScheduler alphaVantageScheduler) {
        this.alphaVantageScheduler = alphaVantageScheduler;
    }

    @GetMapping("/av")
    public List<StockDto> get() {
        return StaticStockDto.STATIC_STOCK_DTO_LIST;
    }

    // TODO 추후 삭제
    @GetMapping("/av-test")
    public String test() {
        alphaVantageScheduler.getStockData();
        return "success!!";
    }
}

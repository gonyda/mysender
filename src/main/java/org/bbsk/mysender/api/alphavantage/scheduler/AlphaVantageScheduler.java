package org.bbsk.mysender.api.alphavantage.scheduler;

import org.bbsk.mysender.api.alphavantage.dto.StaticStockDto;
import org.bbsk.mysender.api.alphavantage.dto.StockDto;
import org.bbsk.mysender.api.alphavantage.dto.StockDataResponseDto;
import org.bbsk.mysender.api.alphavantage.service.AlphaVantageStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AlphaVantageScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlphaVantageScheduler.class);

    private final AlphaVantageStockService alphaVantageStockService;

    public AlphaVantageScheduler(AlphaVantageStockService alphaVantageStockService) {
        this.alphaVantageStockService = alphaVantageStockService;
    }

    @Scheduled(cron = "0 0 7 * * TUE-SAT")
    public void getStockData() {
        String today = alphaVantageStockService.getToday();

        // 기존 데이터 초기화
        StaticStockDto.STATIC_STOCK_DTO_LIST.clear();
        // 주가 데이터 조회
        for (String symbol : StaticStockDto.SYMBOL_LIST) {
            StockDataResponseDto stockData = alphaVantageStockService.getStockData(symbol);
            double todayPrice = alphaVantageStockService.getTodayPrice(stockData, today);

            // 전일 대비 변동률
            double percentageByYesterday = alphaVantageStockService.getPercentageByYesterday(stockData, todayPrice);
            // 10일 평균값 구하기
            double percentageByDaysBefore10 = alphaVantageStockService.getPercentageByDaysBefore(stockData, todayPrice, 10 );
            // 20일 평균값 구하기
            double percentageByDaysBefore20 = alphaVantageStockService.getPercentageByDaysBefore(stockData, todayPrice, 20);
            // 100일 평균값 구하기
            double percentageByDaysBefore100 = alphaVantageStockService.getPercentageByDaysBefore(stockData, todayPrice, 100);

            StaticStockDto.STATIC_STOCK_DTO_LIST.add(
                    new StockDto(symbol,
                            percentageByYesterday,
                            percentageByDaysBefore10,
                            percentageByDaysBefore20,
                            percentageByDaysBefore100)
            );
        }
    }




}

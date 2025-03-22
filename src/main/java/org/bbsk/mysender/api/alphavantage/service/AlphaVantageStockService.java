package org.bbsk.mysender.api.alphavantage.service;

import org.bbsk.mysender.api.alphavantage.constant.AlphaVantageRecord;
import org.bbsk.mysender.api.alphavantage.dto.StaticStockDto;
import org.bbsk.mysender.api.alphavantage.dto.StockDataResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

import static org.bbsk.mysender.api.alphavantage.dto.StockDataResponseDto.*;

@Service
public class AlphaVantageStockService {

    private static final Logger log = LoggerFactory.getLogger(AlphaVantageStockService.class);

    private final AlphaVantageRecord alphaVantageRecord;

    private final RestClient restClient = RestClient.create();

    public AlphaVantageStockService(AlphaVantageRecord alphaVantageRecord) {
        this.alphaVantageRecord = alphaVantageRecord;
    }

    public StockDataResponseDto getStockData(String symbol) {
        return restClient.get()
                .uri("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY" +
                          "&symbol=" + symbol +
                          "&apikey=" + alphaVantageRecord.key())
                .retrieve()
                .body(StockDataResponseDto.class);
    }

    /**
     * 전일 대비 변동률
     * @param stockData
     * @param todayPrice
     * @return
     */
    public double getPercentageByYesterday(StockDataResponseDto stockData, double todayPrice) {
        String yesterday = LocalDate.now().minusDays(2L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        double yesterdayPrice = Double.parseDouble(stockData.getTimeSeriesDaily().get(yesterday).getClose());
        // 전일대비
        return getPercentage(todayPrice, yesterdayPrice);
    }

    /**
     * ??일선 대비 변동률
     * @param stockData
     * @param daysAgo
     * @return
     */
    public double getPercentageByDaysBefore(StockDataResponseDto stockData, double todayPrice, int daysAgo) {
        double avgPrice = stockData.getTimeSeriesDaily().keySet().stream()
                .limit(daysAgo)
                .mapToDouble(date -> {
                    return Double.parseDouble(stockData.getTimeSeriesDaily().get(date).getClose());
                })
                .sum() / daysAgo;

        return getPercentage(todayPrice, avgPrice);
    }

    /**
     * 당일(마지막) 종가 구하기
     * @param stockData
     * @param today
     * @return
     */
    public double getTodayPrice(StockDataResponseDto stockData, String today) {
        DailyPrice dailyPrice = stockData.getTimeSeriesDaily().get(today);
        AtomicLong offset = new AtomicLong(1);

        // 주어진 날짜에 데이터가 없으면 이전 날짜로 이동
        while (dailyPrice == null) {
            today = LocalDate.now().minusDays(offset.incrementAndGet())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dailyPrice = stockData.getTimeSeriesDaily().get(today);
        }

        return Double.parseDouble(dailyPrice.getClose());
    }

    /**
     * 오늘 일자 구하기
     * @return
     */
    public String getToday() {
        // 미국장은 한국시간 기준 전날에 개장
        // 메일 발송시간이 매일 아침 07시 이므로 day -1
        return LocalDate.now().minusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 퍼센테이지 값 구하기
     * @param todayPrice
     * @param targetPrice
     * @return
     */
    private static double getPercentage(double todayPrice, double targetPrice) {
        return Math.round(((todayPrice - targetPrice) / targetPrice) * 100 * 100.0) / 100.0;
    }

}

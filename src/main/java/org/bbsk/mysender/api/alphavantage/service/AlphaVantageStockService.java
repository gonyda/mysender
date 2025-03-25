package org.bbsk.mysender.api.alphavantage.service;

import org.bbsk.mysender.api.alphavantage.constant.AlphaVantageRecord;
import org.bbsk.mysender.api.alphavantage.dto.StockDataResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AlphaVantageStockService {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final RestClient restClient = RestClient.create();

    private final AlphaVantageRecord alphaVantageRecord;

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
     * 당일(마지막) 종가 구하기
     * @param stockData
     * @return
     */
    public double getTodayPrice(StockDataResponseDto stockData) {
        return stockData.getTimeSeriesDaily().keySet().stream()
                .limit(1)
                .map(today -> Double.parseDouble(stockData.getTimeSeriesDaily().get(today).getClose()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("## Time series data is empty."));
    }

    /**
     * 전일(마지막 하루 전 날) 대비 변동률
     *
     * @param stockData
     * @return
     */
    public double getPercentageByYesterday(StockDataResponseDto stockData) {
        List<Double> todayPriceAndYesterdayPrice = stockData.getTimeSeriesDaily().keySet().stream()
                .limit(2)
                .map(date -> Double.parseDouble(stockData.getTimeSeriesDaily().get(date).getClose()))
                .toList();

        if(todayPriceAndYesterdayPrice.size() != 2) {
            throw new IllegalStateException("## Time series data size is less than 2");
        }

        // 전일대비
        return getPercentage(todayPriceAndYesterdayPrice.get(0), todayPriceAndYesterdayPrice.get(1));
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
                .mapToDouble(date -> Double.parseDouble(stockData.getTimeSeriesDaily().get(date).getClose()))
                .sum() / daysAgo;

        return getPercentage(todayPrice, avgPrice);
    }

    /**
     * 10일 평균거래량 대비 당일거래량
     * @param stockData
     * @return
     */
    public double getPercentageVolumeBy10DaysBefore(StockDataResponseDto stockData) {
        // 10일 평균 거래량 계산
        long averageVolumeBy10Days = stockData.getTimeSeriesDaily().keySet().stream()
                .limit(10)
                .mapToLong(date -> Long.parseLong(stockData.getTimeSeriesDaily().get(date).getVolume()))
                .sum() / 10;

        // 오늘 거래량 계산
        long todayVolume = stockData.getTimeSeriesDaily().keySet().stream()
                .findFirst()
                .map(date -> Long.parseLong(stockData.getTimeSeriesDaily().get(date).getVolume()))
                .orElseThrow(() -> new IllegalStateException("## getPercentageVolumeBy10DaysBefore"));

        return getPercentage(todayVolume, averageVolumeBy10Days);
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

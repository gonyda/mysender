package org.bbsk.mysender.api.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

// 전체 응답을 담는 API DTO 클래스
public class StockDataResponseDto {

    @JsonProperty("Meta Data")
    private MetaData metaData;

    @JsonProperty("Time Series (Daily)")
    private Map<String, DailyPrice> timeSeriesDaily;

    // Getters and Setters
    public MetaData getMetaData() {
        return metaData;
    }
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
    public Map<String, DailyPrice> getTimeSeriesDaily() {
        return timeSeriesDaily;
    }
    public void setTimeSeriesDaily(Map<String, DailyPrice> timeSeriesDaily) {
        this.timeSeriesDaily = timeSeriesDaily;
    }

    @Override
    public String toString() {
        return "StockDataDto{" +
                "metaData=" + metaData +
                ", timeSeriesDaily=" + timeSeriesDaily +
                '}';
    }

    // 메타데이터 정보를 담는 클래스
    public static class MetaData {

        @JsonProperty("1. Information")
        private String information;

        @JsonProperty("2. Symbol")
        private String symbol;

        @JsonProperty("3. Last Refreshed")
        private String lastRefreshed;

        @JsonProperty("4. Output Size")
        private String outputSize;

        @JsonProperty("5. Time Zone")
        private String timeZone;

        // Getters and Setters

        public String getInformation() {
            return information;
        }
        public void setInformation(String information) {
            this.information = information;
        }
        public String getSymbol() {
            return symbol;
        }
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        public String getLastRefreshed() {
            return lastRefreshed;
        }
        public void setLastRefreshed(String lastRefreshed) {
            this.lastRefreshed = lastRefreshed;
        }
        public String getOutputSize() {
            return outputSize;
        }
        public void setOutputSize(String outputSize) {
            this.outputSize = outputSize;
        }
        public String getTimeZone() {
            return timeZone;
        }
        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        @Override
        public String toString() {
            return "MetaData{" +
                    "information='" + information + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", lastRefreshed='" + lastRefreshed + '\'' +
                    ", outputSize='" + outputSize + '\'' +
                    ", timeZone='" + timeZone + '\'' +
                    '}';
        }
    }

    // 일별 주가 정보를 담는 클래스
    public static class DailyPrice {

        @JsonProperty("1. open")
        private String open;

        @JsonProperty("2. high")
        private String high;

        @JsonProperty("3. low")
        private String low;

        @JsonProperty("4. close")
        private String close;

        @JsonProperty("5. volume")
        private String volume;

        // Getters and Setters
        public String getOpen() {
            return open;
        }
        public void setOpen(String open) {
            this.open = open;
        }
        public String getHigh() {
            return high;
        }
        public void setHigh(String high) {
            this.high = high;
        }
        public String getLow() {
            return low;
        }
        public void setLow(String low) {
            this.low = low;
        }
        public String getClose() {
            return close;
        }
        public void setClose(String close) {
            this.close = close;
        }
        public String getVolume() {
            return volume;
        }
        public void setVolume(String volume) {
            this.volume = volume;
        }

        @Override
        public String toString() {
            return "DailyPrice{" +
                    "open='" + open + '\'' +
                    ", high='" + high + '\'' +
                    ", low='" + low + '\'' +
                    ", close='" + close + '\'' +
                    ", volume='" + volume + '\'' +
                    '}';
        }
    }
}

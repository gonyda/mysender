package org.bbsk.mysender.api.alphavantage.dto;

public record StockDto(String symbol, double percentageByYesterday, double percentageByDaysBefore10,
                       double percentageByDaysBefore20, double percentageByDaysBefore100) {

    @Override
    public String toString() {
        return "HtmlDto{" +
                "symbol='" + symbol + '\'' +
                ", percentageByYesterday=" + percentageByYesterday +
                ", percentageByDaysBefore10=" + percentageByDaysBefore10 +
                ", percentageByDaysBefore20=" + percentageByDaysBefore20 +
                ", percentageByDaysBefore100=" + percentageByDaysBefore100 +
                '}';
    }
}

package org.bbsk.mysender.api.alphavantage.dto;

public record StockDto(String symbol, double todayPrice, double percentageByYesterday, double percentageByDaysBefore10,
                       double percentageByDaysBefore20, double percentageByDaysBefore100) {
}

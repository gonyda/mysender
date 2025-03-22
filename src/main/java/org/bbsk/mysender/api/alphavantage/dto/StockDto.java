package org.bbsk.mysender.api.alphavantage.dto;

public class StockDto {

    private final String symbol;
    private final double percentageByYesterday;
    private final double percentageByDaysBefore10;
    private final double percentageByDaysBefore20;
    private final double percentageByDaysBefore100;

    public StockDto(String symbol
            , double percentageByYesterday
            , double percentageByDaysBefore10
            , double percentageByDaysBefore20
            , double percentageByDaysBefore100) {
        this.symbol = symbol;
        this.percentageByYesterday = percentageByYesterday;
        this.percentageByDaysBefore10 = percentageByDaysBefore10;
        this.percentageByDaysBefore20 = percentageByDaysBefore20;
        this.percentageByDaysBefore100 = percentageByDaysBefore100;
    }

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

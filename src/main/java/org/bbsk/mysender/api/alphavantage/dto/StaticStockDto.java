package org.bbsk.mysender.api.alphavantage.dto;

import java.util.ArrayList;
import java.util.List;

public class StaticStockDto {

    // 주식 변동률 데이터 (DB 대용)
    public static final List<StockDto> STATIC_STOCK_DTO_LIST = new ArrayList<>();

    // 관심종목
    public static final List<String> SYMBOL_LIST = List.of(
            "SPY",    // 1
            "QQQ",    // 2
            "TLT",    // 3
            "AAPL",   // 4
            "MSFT",   // 5
            "GOOGL",  // 6
            "AMZN",   // 7
            "META",   // 8
            "NVDA",   // 9
            "TSLA",   // 10
            "PLTR",   // 11
            "UBER",   // 12
            "TEM",    // 13
            "RXRX",   // 14
            "PEP",    // 15
            "KO",     // 16
            "O",      // 17
            "JPM",    // 18
            "BAC",    // 19
            "MS",     // 20
            "BLK",    // 21
            "BRK.B",  // 22
            "AXP",    // 23
            "V",      // 24
            "MA"      // 25
    );
}

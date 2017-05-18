package com.udacity.stockhawk.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by dariomartin on 19/5/17.
 */

public class QuoteBuilder {
    public static Map<String, Stock> generateFakeQuotes(String[] stockArray) {

        Map<String, Stock> fakeQuotes = new HashMap<>();

        for (String stockSymbol : stockArray){

            Stock stock = new Stock(stockSymbol);
            StockQuote stockQuote = new StockQuote(stockSymbol);
            stockQuote.setPrice(BigDecimal.valueOf(50));
            stockQuote.setPreviousClose(BigDecimal.valueOf(10));

            stock.setName(stockSymbol.toLowerCase());
            stock.setQuote(stockQuote);
            stock.setHistory(new ArrayList<HistoricalQuote>());

            fakeQuotes.put(stockSymbol, stock);
        }

        return fakeQuotes;
    }
}

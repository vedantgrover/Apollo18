package com.freyr.apollo18.data.records.business;

public record Stock(String ticker, int currentPrice, int previousPrice, int change, String arrowEmoji) {
}

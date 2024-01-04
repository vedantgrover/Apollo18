package com.freyr.apollo18.data.records.business;

import org.bson.Document;

public record Stock(String ticker, int currentPrice, int previousPrice, int change, String arrowEmoji) {
}

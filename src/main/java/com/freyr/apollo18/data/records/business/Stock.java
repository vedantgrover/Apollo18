package com.freyr.apollo18.data.records.business;

import org.bson.Document;

public record Stock(String ticker, int currentPrice, int previousPrice, int change, String arrowEmoji) {
    public Stock(Document document) {
        this(document.getString("ticker"), document.getInteger("currentPrice"), document.getInteger("previousPrice"), document.getInteger("change"), document.getString("arrowEmoji"));
    }
}

package com.freyr.apollo18.data.records;

public record Transaction(String userID, int byteExchange, int previousBal, int newBal, String transactionType, String transactionDate) {
}

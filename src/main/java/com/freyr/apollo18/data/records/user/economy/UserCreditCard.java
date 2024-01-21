package com.freyr.apollo18.data.records.user.economy;

public record UserCreditCard(boolean hasCard, int currentBalance, int totalBalance, String expirationDate) {
}

package com.freyr.apollo18.data.records.user.economy;

import java.util.List;

public record UserEconomy(int balance, int bank, UserJob job, UserCard card, List<UserStock> stocks) {
}

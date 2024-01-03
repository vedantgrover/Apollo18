package com.freyr.apollo18.data.records.user;

import com.freyr.apollo18.data.records.user.economy.UserEconomy;
import com.freyr.apollo18.data.records.user.music.UserMusic;

import java.util.List;

public record User(String userID, List<UserLeveling> leveling, UserEconomy economy, UserMusic music,
                   boolean notifications) {
}

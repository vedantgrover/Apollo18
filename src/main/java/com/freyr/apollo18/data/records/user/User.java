package com.freyr.apollo18.data.records.user;

import com.freyr.apollo18.data.records.user.economy.UserEconomy;
import com.freyr.apollo18.data.records.user.music.UserMusic;

import java.util.ArrayList;

public record User(String userID, ArrayList<UserLeveling> leveling, UserEconomy economy, UserMusic music, boolean notifications) {
}

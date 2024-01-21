package com.freyr.apollo18.data.records.guild;

public record Greeting(boolean onOff, String welcomeChannel, String leaveChannel, String memberCountChannel, String welcomeMessage, String leaveMessage) {
}

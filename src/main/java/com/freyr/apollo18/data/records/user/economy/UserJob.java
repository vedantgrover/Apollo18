package com.freyr.apollo18.data.records.user.economy;

public record UserJob(String businessCode, String jobName, int daysWorked, int daysMissed, boolean worked) {
}

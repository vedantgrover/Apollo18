package com.freyr.apollo18.data.records.business;

import org.bson.Document;

public record Job(String name, String description, int salary, int daysBeforeFire, boolean available) {
}

package com.freyr.apollo18.data.records.business;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public record Business(String name, String stockCode, String owner, String description, String logo, boolean isPublic, List<Job> jobs, Stock stock) {
}

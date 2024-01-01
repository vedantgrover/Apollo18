package com.freyr.apollo18.data.records.business;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public record Business(String name, String stockCode, String owner, String description, String logo, boolean isPublic, List<Job> jobs, Stock stock) {
    public Business(Document document) {
        this(document.getString("name"), document.getString("stockCode"), document.getString("owner"), document.getString("description"), document.getString("logo"), document.getBoolean("public", false), document.getList("jobs", Job.class), document.get("stock", Stock.class));
    }
}

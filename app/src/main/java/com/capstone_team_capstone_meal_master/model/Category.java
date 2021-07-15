package com.capstone_team_capstone_meal_master.model;

public class Category {
    private String id;
    private String category;
    private String url;
    private String description;

    public Category(String id, String category, String url, String description) {
        this.id = id;
        this.category = category;
        this.url = url;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

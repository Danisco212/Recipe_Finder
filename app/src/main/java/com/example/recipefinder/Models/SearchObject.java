package com.example.recipefinder.Models;

public class SearchObject {

    private String searchTerm;
    private int sortOrder;

    public SearchObject(String searchTerm, int sortOrder) {
        this.searchTerm = searchTerm;
        this.sortOrder = sortOrder;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}

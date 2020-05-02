package com.hgo.planassistant.model;


import java.util.ArrayList;
import java.util.List;

public class EmotionalTrendResults {
    private String text;
    private List<EmotionalTrendResultsItems> items = new ArrayList<EmotionalTrendResultsItems>();

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setItems(List<EmotionalTrendResultsItems> items) {
        this.items = items;
    }

    public List<EmotionalTrendResultsItems> getItems() {
        return items;
    }
}

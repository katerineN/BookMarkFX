package com.example.demo1;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BookMark {

    private final StringProperty url;
    private final StringProperty description;
    private final StringProperty category;

    public BookMark(String url, String description, String category) {
        this.url = new SimpleStringProperty(url);
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public String toFileString() {
        return getUrl() + ";" + getDescription() + ";" + getCategory();
    }
}

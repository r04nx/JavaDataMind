package com.javadata.model;

public class Dataset {
    private String type;
    private String name;
    private String color;
    private String uploadedBy;

    // Constructors, getters, and setters
    public Dataset(String type, String name, String color, String uploadedBy) {
        this.type = type;
        this.name = name;
        this.color = color;
        this.uploadedBy = uploadedBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}

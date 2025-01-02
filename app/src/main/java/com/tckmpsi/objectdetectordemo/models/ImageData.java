package com.tckmpsi.objectdetectordemo.models;

public class ImageData {

    private String image;
    private String model_name;

    // Constructor
    public ImageData(String image, String model_name) {
        this.image = image;
        this.model_name = model_name;
    }

    // Getters and setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }
}

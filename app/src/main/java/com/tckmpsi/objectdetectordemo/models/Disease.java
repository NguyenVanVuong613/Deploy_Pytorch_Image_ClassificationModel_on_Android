package com.tckmpsi.objectdetectordemo.models;

import com.google.gson.annotations.SerializedName;

public class Disease {

    private String Disease;
    private double Score;
    private DiseaseDetail Detail;

    // Getter and Setter methods
    public String getDisease() {
        return Disease;
    }

    public void setDisease(String disease) {
        Disease = disease;
    }

    public double getScore() {
        return Score;
    }

    public void setScore(double score) {
        Score = score;
    }

    public DiseaseDetail getDetail() {
        return Detail;
    }

    public void setDetail(DiseaseDetail detail) {
        Detail = detail;
    }

    @Override
    public String toString() {
        return "Disease{" +
                "Disease='" + Disease + '\'' +
                ", Score=" + Score +
                ", Detail=" + Detail +
                '}';
    }
}

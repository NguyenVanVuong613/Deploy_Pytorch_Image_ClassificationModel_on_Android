package com.tckmpsi.objectdetectordemo.models;

import com.google.gson.annotations.SerializedName;

public class DiseaseDetail {
    private double Hac_to;
    private double Vay;
    private double Day;
    private double Khong_benh;
    private double Ung_thu;
    private double Benh_khac;

    // Getter and Setter methods
    public double getHac_to() {
        return Hac_to;
    }

    public void setHac_to(double hac_to) {
        Hac_to = hac_to;
    }

    public double getVay() {
        return Vay;
    }

    public void setVay(double vay) {
        Vay = vay;
    }

    public double getDay() {
        return Day;
    }

    public void setDay(double day) {
        Day = day;
    }

    public double getKhong_benh() {
        return Khong_benh;
    }

    public void setKhong_benh(double khong_benh) {
        Khong_benh = khong_benh;
    }

    public double getUng_thu() {
        return Ung_thu;
    }

    public void setUng_thu(double ung_thu) {
        Ung_thu = ung_thu;
    }

    public double getBenh_khac() {
        return Benh_khac;
    }

    public void setBenh_khac(double benh_khac) {
        Benh_khac = benh_khac;
    }

    @Override
    public String toString() {
        return "DiseaseDetail{" +
                "Hac_to=" + Hac_to +
                ", Vay=" + Vay +
                ", Day=" + Day +
                ", Khong_benh=" + Khong_benh +
                ", Ung_thu=" + Ung_thu +
                ", Benh_khac=" + Benh_khac +
                '}';
    }
}
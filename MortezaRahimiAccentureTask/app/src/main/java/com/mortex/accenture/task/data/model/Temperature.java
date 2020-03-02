package com.mortex.accenture.task.data.model;

public class Temperature {
    private double temp;
    private double feels_like;
    private double temp_min;
    private double temp_max;
    private long pressure;
    private int humidity;

    public double getTemp() {
        return temp;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public long getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public Temperature(double temp) {
        this.temp = temp;
    }
}

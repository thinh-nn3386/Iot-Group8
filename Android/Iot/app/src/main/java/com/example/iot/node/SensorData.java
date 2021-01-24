package com.example.iot.node;

public class SensorData {
    private double temperature;
    private double humidity;
    private double light;
    public SensorData(double temperature, double humidity, double light){
        this.humidity = humidity;
        this.light = light;
        this.temperature = temperature;
    }
    public SensorData(){

    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getLight() {
        return light;
    }

    public void setLight(double light) {
        this.light = light;
    }

}

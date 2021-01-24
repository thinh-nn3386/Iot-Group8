package com.example.iot.node;

import java.util.ArrayList;
import java.util.List;

public class DataPoint {
    private long time;
    private List<SensorData> data = new ArrayList<>();

    public DataPoint(long time){
        this.time = time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
    public void addData(SensorData sensorData){
        this.data.add(sensorData);
    }

    public List<SensorData> getData() {
        return data;
    }
}

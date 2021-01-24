package com.example.iot.node;

import java.util.ArrayList;
import java.util.List;

public class SensorNode {
    private int id;
    private SensorData sensorData;

    public SensorNode(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
}


package Entity;

public class SensorData {
    private double temperature;
    private double humidity;
    private double light;

    public SensorData(double temperature, double humidity, double light){
        this.humidity = humidity;
        this.temperature  = temperature;
        this.light = light;
    }
    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setLight(double light) {
        this.light = light;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getLight() {
        return light;
    }

    public double getTemperature() {
        return temperature;
    }
}

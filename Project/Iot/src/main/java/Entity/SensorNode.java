package Entity;

public class SensorNode extends SensorData {
    private int id;

    public SensorNode(int id, double temperature, double himidity, double light){
        super(temperature,himidity,light);
        this.id = id;
    }



}

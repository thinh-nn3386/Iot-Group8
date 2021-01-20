package subsystem.firebaseSubsystem;

public class DataPoint {
    private int xValue, yValue;
    public  DataPoint(int x, int y){
        this.xValue =x;
        this.yValue = y;
    }
    public DataPoint(){

    }
    public int getxValue() {
        return xValue;
    }

    public int getyValue() {
        return yValue;
    }
}


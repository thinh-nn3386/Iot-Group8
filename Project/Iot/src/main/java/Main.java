import Entity.SensorData;
import subsystem.ICloud;
import subsystem.firebaseSubsystem.Firebase;
import subsystem.initialStateSubsystem.InitialCloud;
import net.tinyos.packet.PacketSource;
import net.tinyos.util.PrintStreamMessenger;
import serial.Serial;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        ICloud cloud = new Firebase();
        Serial port = new Serial(args);
        PacketSource packetSource = port.getPacketSource();
        HashMap<String,Object > map = new HashMap<>();
        try {
            packetSource.open(PrintStreamMessenger.err);
            while (true) {
                byte[] data = packetSource.readPacket();
                //System.out.println(data.length);
                for (int i = 8; i < data.length; i += 8) {
                    int id = port.byteToInt(data[i], data[i + 1]);
                    double temp = port.temperature(data[i + 2], data[i + 3]);
                    double humi = port.humidity(data[i + 4], data[i + 5]);
                    double light = port.light1(data[i+6],data[i+7]);

                    SensorData sensorData = new SensorData(temp, humi, light);
                    map.put(String.valueOf(id),sensorData);
                    System.out.println(id + " " + temp + " " + humi + " " + light);
                }
                cloud.sendToCloud( map);
                System.out.flush();
                map.clear();
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

    }
}

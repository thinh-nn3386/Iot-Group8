import initialState.Cloud;
import net.tinyos.packet.PacketSource;
import net.tinyos.util.PrintStreamMessenger;
import serial.Serial;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {


        Cloud<Double> cloud = new Cloud<>("src/main/java/initialKey.json");
        Serial port = new Serial(args);
        PacketSource packetSource = port.getPacketSource();
        HashMap<String, Double> map = new HashMap<>();
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
                    //map.put("id", Double.valueOf(id));
                    map.put("temperature" + id, temp);
                    map.put("humidity" + id, humi);
                    map.put("light" +id,light);
                    System.out.println(id + " " + temp + " " + humi + " " + light);
                }
                cloud.send(map);
                System.out.flush();
                map.clear();
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        } finally {
            cloud.close();
        }

    }
}

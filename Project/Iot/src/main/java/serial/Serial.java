package serial;

import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PacketSource;

public class Serial {
    private PacketSource packetSource;
    public Serial(String[] serialComm) {
        setPacketSource(serialComm);
    }
    public void setPacketSource(String[] serialComm){
        String var1 = null;
        if (serialComm.length == 2 && serialComm[0].equals("-comm")) {
            var1 = serialComm[1];
        } else if (serialComm.length > 0) {
            System.err.println("usage: java net.tinyos.tools.Listen [-comm PACKETSOURCE]");
            System.err.println("       (default packet source from MOTECOM environment variable)");
            System.exit(2);
        }
        if (var1 == null) {
            packetSource = BuildSource.makePacketSource();
        } else {
            packetSource = BuildSource.makePacketSource(var1);
        }
        if (packetSource == null) {
            System.err.println("Invalid packet source (check your MOTECOM environment variable)");
            System.exit(2);
        }
    }
    public PacketSource getPacketSource() {
        return this.packetSource;
    }
    public double temperature(byte a, byte b ){
        int rawData = byteToInt(a,b);
        return  format(-39.6 + 0.01*rawData);
    }
    public double humidity(byte a, byte b){
        int rawData = byteToInt(a,b);
        double humi = -2.0468 + 0.0367*rawData -1.5955*Math.pow(10,-6)*rawData*rawData;
        return format(humi);
    }
    public double light1(byte a, byte b){
        int rawData = byteToInt(a,b);
        double lux=  2.5*((rawData)/4096.0)*6250;
        return format(lux);
    }

    public int byteToInt(byte a,byte b){
        return ((a & 0xff)<<8)|(b & 0xff);
    }

    public double format(double val){
        String temp= String.format("%.3f", val);
        return Double.parseDouble(temp);
    }

}


package subsystem;

import java.util.Map;

public interface ICloud {
    public abstract void sendToCloud(Map<String,Object> data);
}

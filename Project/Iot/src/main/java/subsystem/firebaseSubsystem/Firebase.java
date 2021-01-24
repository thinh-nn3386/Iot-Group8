package subsystem.firebaseSubsystem;
import subsystem.ICloud;
import java.util.Map;


public class Firebase implements ICloud {

    private LocalApp localApp;

    public Firebase(){
        localApp = new LocalApp();
    }

    @Override
    public void sendToCloud(Map<String, Object> data) {
        localApp.createData(data);
    }
}




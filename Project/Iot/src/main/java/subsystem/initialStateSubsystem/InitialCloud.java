package subsystem.initialStateSubsystem;

import Entity.SensorData;
import com.google.gson.Gson;
import subsystem.ICloud;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class InitialCloud implements ICloud {
    private static final String INITIAL_STATE_KEY_PATH = "src/main/java/subsystem/initialStateSubsystem/initialKey.json";
    private static InitialCloud instance = null;
    
    private API account;
    private Bucket bucket;
    private void createBucket(String accessKey, String bucketKey){
        account = new API(accessKey);
        bucket = new Bucket(bucketKey);
        account.createBucket(bucket);
    }
//    public Cloud(String accessKey, String bucketKey) {
//        createBucket(accessKey,bucketKey);
//    }

    private InitialCloud(String jsonPath){
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(jsonPath));

            Map<String, String> map = gson.fromJson(reader, Map.class);
            createBucket(map.get("Access Key"),map.get("Bucket Key"));
            //System.out.println("done");
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public InitialCloud getInstance() {
        if (instance == null)
            instance = new InitialCloud(INITIAL_STATE_KEY_PATH);
        return instance;
    }

    @Override
    public void sendToCloud(Map<String, Object> data) {
        Data[] motedata = new Data[data.size()];
        int num = 0;
        for(Map.Entry entry : data.entrySet()){
            motedata[num] = new Data((String) entry.getKey(), entry.getValue());
            num++;
            //System.out.print(entry.getKey() + "   " + entry.getValue());
        }
        account.createBulkData(bucket, motedata);
    }



}

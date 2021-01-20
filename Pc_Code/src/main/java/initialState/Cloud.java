package initialState;

import com.google.gson.Gson;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Cloud<T> {

    
    private API account;
    private Bucket bucket;
    private void createBucket(String accessKey, String bucketKey){
        account = new API(accessKey);
        bucket = new Bucket(bucketKey);
        account.createBucket(bucket);
    }
    public Cloud(String accessKey, String bucketKey) {
        createBucket(accessKey,bucketKey);
    }

    public Cloud(String jsonPath){
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
    public void send(Map<String, T> moteData) {
        Data[] data = new Data[moteData.size()];
        int num = 0;
        for(Map.Entry entry : moteData.entrySet()){
            data[num] = new Data((String) entry.getKey(), entry.getValue());
            num++;
            //System.out.print(entry.getKey() + "   " + entry.getValue());
        }
        account.createBulkData(bucket, data);
    }
    public void send(String key, T value) {
        Data data = new Data(key,value);
        account.createData(bucket, data);
    }
    public void close(){
        account.terminate();
    }


}

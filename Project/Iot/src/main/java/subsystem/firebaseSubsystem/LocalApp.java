package subsystem.firebaseSubsystem;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class LocalApp extends RealtimeDatabase implements IApp{
    static {
        try {
            FileInputStream serviceAccount = new FileInputStream(SERVER_KEY_PATH);

            // Initialize the app with a service account, granting admin privileges
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    // Or other region, e.g. <databaseName>.europe-west1.firebasedatabase.app
                    .build();
            FirebaseApp.initializeApp(options);

            database = FirebaseDatabase.getInstance();
            ref = database.getReference(REFERENCE);
        } catch (FileNotFoundException exception){
            exception.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void createData(Map<String, Object> data) {
        long time = System.currentTimeMillis();
        ref.child(String.valueOf(time)).setValueAsync(data);
    }

    @Override
    public String getServerKeyPath() {
        return SERVER_KEY_PATH;
    }
}

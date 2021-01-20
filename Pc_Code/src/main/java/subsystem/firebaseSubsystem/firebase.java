package subsystem.firebaseSubsystem;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.database.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//("https://test-c54a2-default-rtdb.firebaseio.com/")
public class firebase {
    public static void main(String[] args) throws Exception {
        ArrayList<DataPoint> dataVals = new ArrayList<DataPoint>();
        FileInputStream serviceAccount = new FileInputStream("src/main/java/subsystem/firebaseSubsystem/serverKey.json");

        // Initialize the app with a service account, granting admin privileges
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://test-c54a2-default-rtdb.firebaseio.com")
                // Or other region, e.g. <databaseName>.europe-west1.firebasedatabase.app
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(options);
        System.out.println(app.getName());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Test3");

        firebase fibase = new firebase();

        while(true) {

            fibase.insertData(ref);
            //fibase.wait(1000);
            Thread.sleep(3000);
        }
    }
    public void insertData(DatabaseReference ref) {
        long time = System.currentTimeMillis();
        ApiFuture<Void> a = ref.child(String.valueOf(time)).setValueAsync((int) (Math.random() * 100));

    }
    }




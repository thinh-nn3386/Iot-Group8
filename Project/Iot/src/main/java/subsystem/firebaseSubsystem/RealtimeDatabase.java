package subsystem.firebaseSubsystem;

import com.google.api.core.ApiFuture;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeDatabase implements IDatabase {
    static FirebaseDatabase database;
    static DatabaseReference ref;

    public void createData(DatabaseReference ref) {
        long time = System.currentTimeMillis();
        ApiFuture<Void> a = ref.child(String.valueOf(time)).setValueAsync((int) (Math.random() * 100));

    }
    @Override
    public String getDatabaseUrl() {
        return DATABASE_URL;
    }
}

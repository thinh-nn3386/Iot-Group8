package subsystem.initialStateSubsystem;

public class Bucket implements Events {
  private final static String BUCKET_API_URL = API_BASEURL + "buckets";

  //key cho mỗi bucket
  private String bucketKey;;

  public Bucket(String bucketKey) {
    this.bucketKey = bucketKey;
  }
  public String getKey() { return bucketKey; }

  public String getEndpoint() {
    return BUCKET_API_URL;
  }
}

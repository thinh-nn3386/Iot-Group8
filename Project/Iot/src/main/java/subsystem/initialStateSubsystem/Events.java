package subsystem.initialStateSubsystem;

interface Events {
  //base url for Initial state (https://www.initialstate.com/) connection
  final static String API_BASEURL = "https://groker.init.st/api/";

  //API Endpoint: url for http connection
  String getEndpoint();
}

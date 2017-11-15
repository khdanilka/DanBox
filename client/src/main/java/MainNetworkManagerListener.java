import java.util.ArrayList;

public interface MainNetworkManagerListener {

    void listClientsFiles(ArrayList<String> arrayList);
    void listServerFiles(ArrayList<String> arrayList);
    void addingFileToServerResponse(String msg);
    void gettingFileFromServerResponse(String msg);

}

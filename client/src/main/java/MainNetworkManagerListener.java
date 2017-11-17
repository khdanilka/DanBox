import java.util.ArrayList;

public interface MainNetworkManagerListener {

    void updateListClientsFiles(ArrayList<String> arrayList);
    void updateListServerFiles(ArrayList<String> arrayList);
    void addingFileToServerResponse(String msg);
    void gettingFileFromServerResponse(String msg);

}

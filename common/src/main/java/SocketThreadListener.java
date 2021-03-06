import java.io.File;

public interface SocketThreadListener {
    void readySocketClientThread(SocketThread socketThread);
    boolean checkUserInBD(String login, String pass);
    void auth_answer(String msg);
    void sendFilesToServer();
    void getFilesFromServer();

    void handleIncomingMessage(String str, SocketThread socketThread);
    File[] getListOfFilesWithPath(String url);
    void onStopSocketThread(SocketThread socketThread);
}

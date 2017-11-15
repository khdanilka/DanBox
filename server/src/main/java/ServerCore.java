
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class ServerCore implements ServerSocketThreadListener, SocketThreadListener {

    private int port;
    private ScanPortServerThread serverSocketThread;
    private Vector<ServerSocketThread> clients = new Vector<>();
    private String[] users = {"client6","123"};

    public ServerCore(int port) {
        this.port = port;
    }

    public void startListening(){
        if (serverSocketThread!= null && serverSocketThread.isAlive()) {
            //putLog("сервер уже запущен");
            System.out.println("сервер уже запущен");
            return;
        }
        //putLog("сервер запущен");
        System.out.println("сервер запущен");
        serverSocketThread = new ScanPortServerThread(this,port);
    }

    public void stopListening(){

    }

    @Override
    public void handleIncomingMessage(String str, SocketThread socketThread) {

        ServerSocketThread serverSocketThread = (ServerSocketThread) socketThread;

        System.out.println(str);
        String[] splitArr = str.split(Messages.DEL);

        switch (splitArr[0]) {
            case Messages.POST_FILE:
                try {
                    socketThread.saveDataToHost(splitArr[1],splitArr[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Messages.GET_FILE:
                try {
                    socketThread.sendDataToHost(splitArr[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Messages.AUTH_REQUEST:
                serverSocketThread.handleAuthRequest(splitArr[1],splitArr[2]);
                break;
            case Messages.GET_FILES_LIST:
                serverSocketThread.sendListOfFiles();
            default:
                //System.out.println("UNKHOWN request");
        }

    }

    /////forserver.ServerSocketThreadListener/////
    @Override
    public void socketAccepted(Socket socket) {
        new ServerSocketThread(this,socket);
    }


    /////common.SocketThreadListener////////
    @Override
    public void readySocketClientThread(SocketThread socketThread) {

        System.out.println("На стороне сервера есть сокет");

        clients.add((ServerSocketThread)socketThread);
    }

    @Override
    public boolean checkUserInBD(String login, String pass) {

        if (users[0].equals(login) && users[1].equals(pass)) return true;

        return false;
    }

    @Override
    public void auth_answer(String msg) {
        System.out.println(msg);
    }

    @Override
    public void sendFilesToServer() {
        System.out.println("сервер пока не реагирует на сообщение Success");
    }

    @Override
    public void getFilesFromServer() {
        System.out.println("сервер пока не реагирует на сообшение о получение файлов");
    }

    @Override
    public File[] getListOfFilesWithPath(String url) {
        return new File(url).listFiles();
    }

    


}

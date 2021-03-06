
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

public class ServerCore implements ServerSocketThreadListener, SocketThreadListener {

    private int port;
    private ScanPortServerThread serverSocketThread;
    private Vector<ServerSocketThread> clients = new Vector<>();
    //private String[] users = {"client6","123"};

    SQLAutorizeManager sqlAutorizeManager = new SQLAutorizeManager();


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
        //System.out.println(str);
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
                break;
            case Messages.DELETE_FILE_FROM_SERVER:
                //serverSocketThread.deleteFileFromServerDirectory(splitArr[1]);
                deleteFileFromServerDirectory(splitArr[1],serverSocketThread);
                break;
            case Messages.CLIENT_QUIT:
                serverSocketThread.close();
                break;
            default:
                //System.out.println("UNKHOWN request");
                //serverSocketThread.close();
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
    public void onStopSocketThread(SocketThread socketThread) {
        clients.remove(socketThread);
        System.out.println("клиент " + socketThread.client_name + " отключился");
    }

    @Override
    public boolean checkUserInBD(String login, String pass) {

//        if (users[0].equals(login) && users[1].equals(pass)) return true;
//
//        return false;
        sqlAutorizeManager.init();
        if (sqlAutorizeManager.getNick(login,pass)!= null) return true;
        sqlAutorizeManager.dispose();
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


    public void deleteFileFromServerDirectory(String fileName, ServerSocketThread socketThread){

        String stringPath = ServerSocketThread.beginPath + socketThread.client_name + "/" + fileName;
        Path pathToDel = Paths.get(URI.create("file:" + stringPath));

        try {
            if (Files.deleteIfExists(pathToDel)) socketThread.sendSuccessDeleted();
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }

    }


}

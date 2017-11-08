
import java.net.Socket;
import java.util.Vector;

public class ServerCore implements ServerSocketThreadListener, SocketThreadListener {

    private int port;
    private ScanPortServerThread serverSocketThread;
    private Vector<SocketThread> clients = new Vector<>();
    private String[] users = {"client1","123"};

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

    /////forserver.ServerSocketThreadListener/////
    @Override
    public void socketAccepted(Socket socket) {
        new SocketThread(this,socket, true);
    }


    /////common.SocketThreadListener////////
    @Override
    public void readySocketClientThread(SocketThread socketThread) {

        System.out.println("На стороне сервера есть сокет");

        clients.add(socketThread);
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
}

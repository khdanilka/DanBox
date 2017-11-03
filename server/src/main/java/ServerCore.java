
import java.net.Socket;
import java.util.Vector;

public class ServerCore implements ServerSocketThreadListener, SocketThreadListener {

    private int port;
    private ServerSocketThread serverSocketThread;
    private Vector<SocketThread> clients = new Vector<>();

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
        serverSocketThread = new ServerSocketThread(this,port);
    }

    public void stopListening(){

    }

    /////forserver.ServerSocketThreadListener/////
    @Override
    public void socketAccepted(Socket socket) {
        new SocketThread(this,socket);
    }


    /////common.SocketThreadListener////////
    @Override
    public void readySocketClientThread(SocketThread socketThread) {

        System.out.println("На стороне сервера есть сокет");

        clients.add(socketThread);
    }
}

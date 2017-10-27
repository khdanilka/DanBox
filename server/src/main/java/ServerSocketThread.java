import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {

    private int port = 4444;
    private ServerSocketThreadListener eventListener;

    public ServerSocketThread(ServerSocketThreadListener eventListener,int port) {
        this.eventListener = eventListener;
        this.port = port;
        start();
    }

    @Override
    public void run() {

        System.out.println("ServerSocket started" + this.getName());

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //serverSocket.setSoTimeout(timeout);
            //eventListener.onServerSocketThreadReady(this,serverSocket);
            while (!isInterrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    //eventListener.onTimeOutAccept(this,serverSocket);
                    continue;
                }
                eventListener.socketAccepted(socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

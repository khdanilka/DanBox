import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements SocketThreadListener{

    private SocketThread socketThread;

    public static void main(String[] args) throws IOException {

        String host = "127.0.0.1";
        int port = 4444;

        Client cl = new Client();

        try{
            Socket socket = new Socket(host,port);
            cl.socketThread = new SocketThread(cl,socket);
            //cl.socketThread.sendDataToHost();
            cl.socketThread.getRequest();
        } catch (IOException e){
            e.printStackTrace();
        }

    }



    @Override
    public void readySocketClientThread(SocketThread socketThread) {
        System.out.println("Socket Thread created for client");
    }
}
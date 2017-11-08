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
            cl.socketThread = new SocketThread(cl,socket, false);
            //cl.socketThread.sendDataToHost();
            cl.auth_request();
            cl.socketThread.sendDataToHost("dracula.jpg");
            cl.socketThread.sendDataToHost("text.txt");
            cl.socketThread.sendDataToHost("123.pdf");
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void readySocketClientThread(SocketThread socketThread) {
        System.out.println("Socket Thread created for client");
        //auth_request();
    }

    private void auth_request() {

        String login = "client1";
        String pass = "123";

        socketThread.sendAuthRequest(login,pass);

    }

    @Override
    public void auth_answer(String msg){
        System.out.println(msg);
    }


    @Override
    public boolean checkUserInBD(String login, String pass) {
        return false;
    }
}
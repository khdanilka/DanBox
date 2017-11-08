import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class Client implements SocketThreadListener{

    private SocketThread socketThread;
    private static Vector<String> sendFiles = new Vector<>();
    private static Vector<String> getFiles = new Vector<>();

    public static void main(String[] args) throws IOException {

        String host = "127.0.0.1";
        int port = 4444;

        Client cl = new Client();

        try{
            Socket socket = new Socket(host,port);
            cl.socketThread = new SocketThread(cl,socket, false);
            //cl.socketThread.sendDataToHost();
            cl.auth_request();

//            sendFiles.add("dracula.jpg");
//            sendFiles.add("text.txt");
//            sendFiles.add("123.pdf");
            //cl.sendFilesToServer();

            getFiles.add("dracula.jpg");
            getFiles.add("text.txt");
            getFiles.add("123.pdf");
            cl.getFilesFromServer();

        } catch (IOException e){
            e.printStackTrace();
        }


//        sendFiles.add("dracula.jpg");
//        sendFiles.add("text.txt");
//        sendFiles.add("123.pdf");
//
//        sendFiles.remove(1);
//
//        System.out.println(sendFiles.get(0));




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


    public void sendFilesToServer(){

        if (!sendFiles.isEmpty())
            try {
            socketThread.sendDataToHost(sendFiles.get(0));
            sendFiles.remove(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFilesFromServer(){

        if (!getFiles.isEmpty()) {
            socketThread.getRequest(getFiles.get(0));
            getFiles.remove(0);
        }
    }
}
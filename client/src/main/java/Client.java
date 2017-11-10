import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Client implements SocketThreadListener{

    private ClientSocketThread socketThread;
    private static Vector<String> sendFiles = new Vector<>();
    private static Vector<String> getFiles = new Vector<>();

    public static void main(String[] args) throws IOException {

        String host = "127.0.0.1";
        int port = 4444;

        Client cl = new Client();

        try {

            Socket socket = new Socket(host, port);
            cl.socketThread = new ClientSocketThread(cl, socket);
            //cl.socketThread.sendDataToHost();
            cl.auth_request();

            sendFiles.add("dracula.jpg");
            sendFiles.add("text.txt");
            sendFiles.add("123.pdf");
//            cl.sendFilesToServer();

            getFiles.add("dracula.jpg");
            getFiles.add("text.txt");
            getFiles.add("123.pdf");
//            cl.getFilesFromServer();
        } catch (IOException e){
            e.printStackTrace();
        }

    }


    @Override
    public File[] getListOfFilesWithPath(String url) {

        return new File(url).listFiles();

    }



    @Override
    public void handleIncomingMessage(String str, SocketThread socketThread) {

        ClientSocketThread clientSocketThread = (ClientSocketThread) socketThread;

        System.out.println(str);
        String[] splitArr = str.split(Messages.DEL);

        switch (splitArr[0]) {
            case Messages.POST_FILE:
                try {
                    clientSocketThread.saveDataToHost(splitArr[1],splitArr[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Messages.AUTH_ACCEPT:
                clientSocketThread.handleAuthAnswer(splitArr[1]);
                auth_answer("успех");
                break;
            case Messages.AUTH_ERROR:
                auth_answer("авторизация не удалась");
                break;
            case Messages.SUCCESS:
                sendFilesToServer();
                break;
            case Messages.FILE_LIST:
                printFileList(splitArr);
                break;
            default:
                //System.out.println("UNKHOWN request");
        }

    }

    void printFileList(String[] splitArr){

        if (splitArr[1].equals("0"))
            System.out.println("на сервере ничего нету");
        else {
            for(int i = 2; i < splitArr.length; i++){
                System.out.println(splitArr[i]);
            }
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

        if (msg.equals("успех")) {
            //getFilesFromServer();
            //getServerFilesList();
            sendFilesToServer();
        }
        else  System.out.println(msg);
    }

    public void getServerFilesList(){
        socketThread.getFilesRequest();
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
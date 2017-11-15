import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ClientSocketThread extends SocketThread {

    public ClientSocketThread(SocketThreadListener eventListener, Socket socket) {
        super(eventListener, socket, false);
    }

    //client method
    void getRequest(String nameOfFile){

        try {
            byte[] serviceB = Messages.messageGet(nameOfFile);
            synchronized (out) {
                out.write(serviceB);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //client method
    void sendAuthRequest(String login, String pass){

        byte[] auth = Messages.messageAuth(login,pass);
        try {
            synchronized (out) {
                out.write(auth);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //client method
    void handleAuthAnswer(String login){
        System.out.println("обработка входящего сообщения - " + login);
        client_name = login;
        eventListener.auth_answer("на клиенте все ок");
    }

    void getFilesRequest(){
        byte[] auth = Messages.messageGetFilesList();
        try {
            synchronized (out) {
                out.write(auth);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getListOfFileFromServer(String size) {

        if (size.equals("0")) return "";

        byte[] bytes = new byte[Integer.valueOf(size) + 10];
        String str = "";
        try {
            System.out.println("прочитано байт " + in.read(bytes));
            str = new String(bytes, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    void socketDeleteFileOnServer(String name){

        byte[] auth = Messages.messageDeleteFile(name);
        try {
            out.write(auth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

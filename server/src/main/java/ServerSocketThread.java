import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ServerSocketThread extends SocketThread {

    public ServerSocketThread(SocketThreadListener eventListener, Socket socket) {
        super(eventListener, socket, true);
    }

    //server method
    void handleAuthRequest(String login, String pass){

        if (eventListener.checkUserInBD(login,pass)) {
            sendAuthAnswerAccept(login);
            client_name = login;
            eventListener.auth_answer("на сервере все ок, юзер на месте " + login);
        } else {
            sendAuthAnswerError();
        }

    }

    //server method
    void sendAuthAnswerAccept(String login){

        byte[] accept = Messages.messageAuthAccepted(login);
        try {
            out.write(accept);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //server method
    void sendAuthAnswerError(){

        byte[] error = Messages.messageAuthError("не верный логин");
        try {
            out.write(error);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void sendListOfFiles(){

        File[] f = eventListener.getListOfFilesWithPath(this.SERVER_PATH + client_name);
        byte[] flb = Messages.messageSendFileList(f);
        try {
            out.write(flb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

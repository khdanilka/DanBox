import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerSocketThread extends SocketThread {


    static final String beginPath =   System.getProperty("user.dir") + "/server/src/main/file_storage/";

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

        StringBuffer stb = new StringBuffer();

        if (f != null) {
            for (int i = 0; i < f.length; i++) {
                if (f[i].isFile()) {
                    if (stb.length()!=0) stb.append(Messages.DEL);
                    stb.append(f[i].getName());
                }
            }
        }

        byte[] bStb = new byte[0];
        try {
            bStb = String.valueOf(stb).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] flb = Messages.messageSendFileList(String.valueOf(bStb.length));

        try {
            out.write(flb);
            //System.out.println(String.valueOf(stb));
            if (stb.length() != 0) out.write(bStb);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



//    public void deleteFileFromServerDirectory(String fileName){
//
//        String stringPath = beginPath + client_name + "/" + fileName;
//        Path pathToDel = Paths.get(URI.create("file:" + stringPath));
//
//        try {
//            if (Files.deleteIfExists(pathToDel)) sendSuccessDeleted();
//        } catch (IOException x) {
//            // File permission problems are caught here.
//            System.err.println(x);
//        }
//
//    }

    public void sendSuccessDeleted(){

        byte[] deleted = Messages.messageDeleted();
        try {
            out.write(deleted);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

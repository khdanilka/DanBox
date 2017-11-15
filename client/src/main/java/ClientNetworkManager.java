import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Vector;

public class ClientNetworkManager implements SocketThreadListener {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 4444;

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";


    private ClientSocketThread socketThread;
    private static Vector<String> sendFiles = new Vector<>();
    private static Vector<String> getFiles = new Vector<>();

    public static void main(String[] args) throws IOException {

//        String host = "127.0.0.1";
//        int port = 4444;
//
//        ClientNetworkManager cl = new ClientNetworkManager(null,null);
//
//        try {
//
//            Socket socket = new Socket(host, port);
//            cl.socketThread = new ClientSocketThread(cl, socket);
//            //cl.socketThread.sendDataToHost();
//            cl.auth_request("client1","123");
////
////            sendFiles.add("dracula.jpg");
////            sendFiles.add("text.txt");
////            sendFiles.add("123.pdf");
//////            cl.sendFilesToServer();
////
////            getFiles.add("dracula.jpg");
////            getFiles.add("text.txt");
////            getFiles.add("123.pdf");
////            cl.getFilesFromServer();
//        } catch (IOException e){
//            e.printStackTrace();
//        }

//        File f = new File(".");
//
//        System.out.println(System.getProperty("user.dir"));
//
//
//
//        Path p = Paths.get(URI.create("file:/Users/android/Documents/2017-09-22.jpg"));
//        Path p1 = Paths.get(URI.create("file:/Users/android/Desktop/DanBox/client/src/main/file_storage/client1/2017-09-22.jpg"));
//
//
//        try {
//            Files.copy(p, p1, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private static ClientNetworkManager clientNetworkManager;
    private static MainNetworkManagerListener clm;
    private static LoginNetworkManagerListener logListener;


    public static ClientNetworkManager getClientNetworkManager(MainNetworkManagerListener clm, LoginNetworkManagerListener loglistner){

       if (clientNetworkManager == null){
            clientNetworkManager = new ClientNetworkManager(clm, loglistner);
       }
       if (ClientNetworkManager.clm == null) ClientNetworkManager.clm = clm;
       if (ClientNetworkManager.logListener == null) ClientNetworkManager.logListener = loglistner;
       return clientNetworkManager;
    }

    private ClientNetworkManager(MainNetworkManagerListener clm, LoginNetworkManagerListener loglistner){
        this(clm);
        ClientNetworkManager.logListener = loglistner;
    }

    private ClientNetworkManager(MainNetworkManagerListener clm){
        ClientNetworkManager.clm = clm;
        Socket socket = null;
        try {
            socket = new Socket(HOST, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketThread = new ClientSocketThread(this, socket);
    }


    @Override
    public File[] getListOfFilesWithPath(String url) {

        return new File(url).listFiles();

    }

    public void addFileToUsersDirectory(String str, String fileName){

        String k = "/client/src/main/file_storage/";
        String dst = System.getProperty("user.dir") + k + socketThread.client_name + "/" + fileName;
        File newF = new File(dst);
        if (!newF.mkdirs()) System.out.println(newF.getPath());

        Path p = Paths.get(URI.create("file:" + dst));
        Path s = Paths.get(URI.create("file:" + str));

        try {
            Files.copy(s, p, StandardCopyOption.REPLACE_EXISTING);
            getClientListOfFilesWithPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileFromUserDirectory(String fileName){

        String k = "/client/src/main/file_storage/";
        String dst = System.getProperty("user.dir") + k + socketThread.client_name + "/" + fileName;

        Path p = Paths.get(URI.create("file:" + dst));

        try {
            if (Files.deleteIfExists(p)) getClientListOfFilesWithPath();
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }

    }

    public void deleteFileFromServer(String filename){

        socketThread.socketDeleteFileOnServer(filename);
    }


    public void getClientListOfFilesWithPath() {

        String url =  socketThread.CLIENT_PATH + socketThread.client_name;

        File[] fl = new File(url).listFiles();
        ArrayList<String> arr = new ArrayList<>();
        if (fl == null) {
            clm.listClientsFiles(arr);
            return;
        }
        for(File f: fl){
            if(!f.getName().startsWith(".")) arr.add(f.getName());
        }
        clm.listClientsFiles(arr);
    }



    @Override
    public void handleIncomingMessage(String str, SocketThread socketThread) {

        ClientSocketThread clientSocketThread = (ClientSocketThread) socketThread;

        String[] splitArr = str.split(Messages.DEL);

        switch (splitArr[0]) {
            case Messages.POST_FILE:
                try {
                    clientSocketThread.saveDataToHost(splitArr[1],splitArr[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clm.gettingFileFromServerResponse(Messages.SUCCESS);
                break;
            case Messages.AUTH_ACCEPT:
                clientSocketThread.handleAuthAnswer(splitArr[1]);
                //auth_answer("успех");
                logListener.authResponse(SUCCESS);
                break;
            case Messages.AUTH_ERROR:
                //auth_answer("авторизация не удалась");
                logListener.authResponse(ERROR);
                break;
            case Messages.SUCCESS:
                clm.addingFileToServerResponse(Messages.SUCCESS);
                sendFilesToServer();
                break;
            case Messages.FILE_LIST:
                String answer = ((ClientSocketThread) socketThread).getListOfFileFromServer(splitArr[1]);
                printFileList(answer);
                break;
            case Messages.FILE_DELETED:
                getServerFilesList();
                break;
            default:
                //System.out.println("UNKHOWN request");
        }

    }

    void printFileList(String answer){

        ArrayList<String> arrayList = new ArrayList<>();

        if (!answer.equals("")) {
            String[] splitArr = answer.split(Messages.DEL);
            for (int i = 0; i < splitArr.length; i++) {
                arrayList.add(splitArr[i]);
            }
        }
        clm.listServerFiles(arrayList);
    }

    @Override
    public void readySocketClientThread(SocketThread socketThread) {
        System.out.println("Socket Thread created for client");
        //auth_request();
    }

    public void auth_request(String login, String pass) {
        //String login = "client1";
        //String pass = "123";
        socketThread.sendAuthRequest(login,pass);
    }

    @Override
    public void auth_answer(String msg){

        if (msg.equals("успех")) {
            //getFilesFromServer();
            //getServerFilesList();
            //sendFilesToServer();
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


    public void addFileInSendRow(String fileName){
        sendFiles.add(fileName);
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

    public void addFileInGetRow(String fileName){
        getFiles.add(fileName);
    }

    public void getFilesFromServer(){

        if (!getFiles.isEmpty()) {
            socketThread.getRequest(getFiles.get(0));
            getFiles.remove(0);
        }
    }
}
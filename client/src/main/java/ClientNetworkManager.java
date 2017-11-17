import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
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

    }

    private static ClientNetworkManager clientNetworkManager;
    private MainNetworkManagerListener mainNetworkManagerListener;
    private LoginNetworkManagerListener logListener;


    public static ClientNetworkManager getClientNetworkManager(){

       if (clientNetworkManager == null){
            clientNetworkManager = new ClientNetworkManager();
       }
       return clientNetworkManager;
    }

    public void setMainNetworkManagerListener(MainNetworkManagerListener mainNetworkManagerListener){
        this.mainNetworkManagerListener = mainNetworkManagerListener;
    }

    public void setLogListener(LoginNetworkManagerListener logListener){
        this.logListener = logListener;
    }


    private ClientNetworkManager(){
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

    private final String pathBegin = System.getProperty("user.dir") + "/client/src/main/file_storage/";

    public void addFileToUsersDirectory(String fileToCopyStringPath, String fileName){

        String newFileStringPath = pathBegin + socketThread.client_name + "/" + fileName;
        File newF = new File(newFileStringPath);
        newF.mkdirs();

        Path sourse = Paths.get(URI.create("file:" + fileToCopyStringPath));
        Path destination = Paths.get(URI.create("file:" + newFileStringPath));

        try {
            Files.copy(sourse, destination, StandardCopyOption.REPLACE_EXISTING);
            getClientListOfFilesWithPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileFromUserDirectory(String fileName){

        String deleteFileStringPath = pathBegin + socketThread.client_name + "/" + fileName;
        Path pathToDel = Paths.get(URI.create("file:" + deleteFileStringPath));

        try {
            if (Files.deleteIfExists(pathToDel)) getClientListOfFilesWithPath();
        } catch (IOException x) {
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
            mainNetworkManagerListener.updateListClientsFiles(arr);
            return;
        }
        for(File f: fl){
            if(!f.getName().startsWith(".")) arr.add(f.getName());
        }
        mainNetworkManagerListener.updateListClientsFiles(arr);
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
                mainNetworkManagerListener.gettingFileFromServerResponse(Messages.SUCCESS);
                break;
            case Messages.AUTH_ACCEPT:
                clientSocketThread.handleAuthAnswer(splitArr[1]);
                logListener.authResponse(SUCCESS);
                break;
            case Messages.AUTH_ERROR:
                logListener.authResponse(ERROR);
                break;
            case Messages.SUCCESS:
                mainNetworkManagerListener.addingFileToServerResponse(Messages.SUCCESS);
                sendFilesToServer();
                break;
            case Messages.FILE_LIST:
                String answer = ((ClientSocketThread) socketThread).getListOfFileFromServer(splitArr[1]);
                handleFileList(answer);
                break;
            case Messages.FILE_DELETED:
                getServerFilesList();
                break;
            default:
                //System.out.println("UNKHOWN request");
        }

    }

    void handleFileList(String answer){

        ArrayList<String> arrayList = new ArrayList<>();
        if (!answer.equals("")) {
            String[] splitArr = answer.split(Messages.DEL);
            arrayList.addAll(Arrays.asList(splitArr));
        }
        mainNetworkManagerListener.updateListServerFiles(arrayList);
    }

    @Override
    public void readySocketClientThread(SocketThread socketThread) {
        System.out.println("Socket Thread created for client");
    }

    public void auth_request(String login, String pass) {
        socketThread.sendAuthRequest(login,pass);
    }

    @Override
    public void auth_answer(String msg){

//        if (msg.equals("успех")) {
//        }
//        else  System.out.println(msg);
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

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        System.out.println("мы завершили работу");
    }

    public void buybuy() {
        socketThread.sendQuit();
        socketThread.close();
    }
}
import java.io.*;
import java.net.Socket;


public class SocketThread extends Thread{

    private final String CLIENT_PATH = "./client/src/main/file_storage/";
    private final String SERVER_PATH = "./server/src/main/file_storage/";


    private final Socket socket;
    protected final SocketThreadListener eventListener;
    protected OutputStream out;
    protected InputStream in;
    protected String client_name;
    protected boolean isServerThread;
    private String rightPath;

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public SocketThread(SocketThreadListener eventListener, Socket socket, boolean isServerThread){
        this.eventListener = eventListener;
        this.socket = socket;
        this.isServerThread = isServerThread;

        if (isServerThread) rightPath = SERVER_PATH;
        else rightPath = CLIENT_PATH;

        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }


    @Override
    public void run() {
        eventListener.readySocketClientThread(this);
        try {
            while (!isInterrupted())
            {
                byte[] b = new byte[Messages.MESSAGE_SIZE];
                in.read(b, 0, b.length);
                String str = new String(b, "UTF-8").trim();
                eventListener.handleIncomingMessage(str, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void saveDataToHost(String fileName, String fileSize) throws IOException {

        //String client = "client5";
        String client = client_name;
        String fop = rightPath + client;
        //String fop ="./client/src/main/file_storage/" + client;
        File myPath = new File(fop);

        if (!myPath.mkdirs()) System.out.println(myPath.getPath());

        OutputStream outS = new FileOutputStream(fop +"/" + fileName);

        byte[] bytes = new byte[16 * 1024];
        long come = Long.valueOf(fileSize);

        while (come > 0) {
            int count = in.read(bytes);
            outS.write(bytes, 0, count);
            come-=count;
        }
        try {
            outS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isServerThread) out.write(Messages.messageSuccess());
        else eventListener.getFilesFromServer();
    }

    protected void sendDataToHost(String nameOfFIle) throws IOException {

        //String client = "client1";
        String client = client_name;
        File file = new File(rightPath + client + "/" + nameOfFIle); // server data
        //File file = new File("./server/src/main/file_storage/" + client + "/" + nameOfFIle);
        System.out.println("размер файла" + file.length());
        try (InputStream ins = new FileInputStream(file))
        {
            byte[] serviceB = Messages.messagePost(nameOfFIle,String.valueOf(file.length()));
            synchronized (out) {
                out.write(serviceB);
            }
            writeDataToOut(ins);
            //out.flush();
        }

    }
//
//    //client method
//    void getRequest(String nameOfFile){
//
//        try {
//            byte[] serviceB = Messages.messageGet(nameOfFile);
//            synchronized (out) {
//                out.write(serviceB);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    //client method
//    void sendAuthRequest(String login, String pass){
//
//        byte[] auth = Messages.messageAuth(login,pass);
//        try {
//            synchronized (out) {
//                out.write(auth);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //client method
//    void handleAuthAnswer(String login){
//        System.out.println("обработка входящего сообщения - " + login);
//        client_name = login;
//        eventListener.auth_answer("на клиенте все ок");
//    }

//    //server method
//    void handleAuthRequest(String login, String pass){
//
//        if (eventListener.checkUserInBD(login,pass)) {
//            sendAuthAnswerAccept(login);
//            client_name = login;
//            eventListener.auth_answer("на сервере все ок, юзер на месте " + login);
//        } else {
//            sendAuthAnswerError();
//        }
//
//    }
//
//    //server method
//    void sendAuthAnswerAccept(String login){
//
//        byte[] accept = Messages.messageAuthAccepted(login);
//        try {
//            out.write(accept);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //server method
//    void sendAuthAnswerError(){
//
//        byte[] error = Messages.messageAuthError("не верный логин");
//        try {
//            out.write(error);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private void writeDataToOut(InputStream ins) throws IOException {
        byte[] bytesData = new byte[16 * 1024];
        int count;
        synchronized (out) {
            while ((count = ins.read(bytesData)) > 0) {
                out.write(bytesData, 0, count);

            }
        }
    }


    public synchronized void close(){
        interrupt();
        try {
            socket.close();
        } catch (IOException e ){
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.Socket;


public class SocketThread extends Thread{

    private final Socket socket;
    private final SocketThreadListener eventListener;
    private OutputStream out;
    private String client_name;
    private boolean isServerThread;

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public SocketThread(SocketThreadListener eventListener, Socket socket, boolean isServerThread){
        this.eventListener = eventListener;
        this.socket = socket;
        this.isServerThread = isServerThread;
        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }


    @Override
    public void run() {
        eventListener.readySocketClientThread(this);
        try {
            InputStream in = socket.getInputStream();
            while (!isInterrupted())
            {
                byte[] b = new byte[Messages.MESSAGE_SIZE];
                in.read(b, 0, b.length);
                String str = new String(b, "UTF-8").trim();
                System.out.println(str);
                String[] splitArr = str.split(Messages.DEL);

                switch (splitArr[0]) {
                    case Messages.POST_FILE:
                        saveDataToHost(in,splitArr[1],splitArr[2]);
                        break;
                    case Messages.GET_FILE:
                        sendDataToHost(splitArr[1]);
                        break;
                    case Messages.AUTH_REQUEST:
                        handleAuthRequest(splitArr[1],splitArr[2]);
                        break;
                    case Messages.AUTH_ACCEPT:
                        handleAuthAnswer(splitArr[1]);
                        break;
                    case Messages.AUTH_ERROR:
                        eventListener.auth_answer("авторизация не удалась");
                        break;
                    default:
                        //System.out.println("UNKHOWN request");
                        continue;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void saveDataToHost(InputStream in, String fileName, String fileSize) throws IOException {

        String client = "client5";
        String fop ="./server/src/main/file_storage/" + client;
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

    }

    void sendDataToHost(String nameOfFIle) throws IOException {

        String client = "client1";
        File file = new File("./client/src/main/file_storage/" + client + "/" + nameOfFIle); // server data
        //File file = new File("./server/src/main/file_storage/" + client + "/" + nameOfFIle);
        System.out.println("размер файла" + file.length());
        try (InputStream in = new FileInputStream(file))
        {
            byte[] serviceB = Messages.messagePost(nameOfFIle,String.valueOf(file.length()));
            synchronized (out) {
                out.write(serviceB);
            }
            writeDataToOut(in);
            //out.flush();
        }

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
        client_name = login;
        eventListener.auth_answer("на клиенте все ок");
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

    private void writeServiceDataToOut(String name, int size) throws IOException {

        byte[] bytes = new byte[size];
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = 0;
        }
        byte[] nameB = name.getBytes("UTF-8");

        for(int i = 0; i <nameB.length; i++){
            bytes[i] = nameB[i];
        }
        out.write(bytes);
    }

    private void writeDataToOut(InputStream in) throws IOException {
        byte[] bytesData = new byte[16 * 1024];
        int count;
        synchronized (out) {
            while ((count = in.read(bytesData)) > 0) {
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

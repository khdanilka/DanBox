import java.io.*;
import java.net.Socket;


public class SocketThread extends Thread{

    private final Socket socket;
    private final SocketThreadListener eventListener;
    private OutputStream out;

    public SocketThread(SocketThreadListener eventListener,Socket socket){
        this.eventListener = eventListener;
        this.socket = socket;
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
                byte[] b = new byte[20];
                in.read(b, 0, b.length);
                String str = new String(b, "UTF-8").trim();

                switch (str) {
                    case Messages.POST_FILE:
                        saveDataToHost(in);
                        break;
                    case Messages.GET_FILE:
                        sendDataToHost();
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

    private void saveDataToHost(InputStream in) throws IOException {

        byte[] b = new byte[100];
        in.read(b, 0, b.length);
        String str = new String(b, "UTF-8");
        String client = "client2";
        // String fop ="./server/src/main/file_storage/" + client;
        String fop ="./client/src/main/file_storage/" + client;
        File myPath = new File(fop);

        if (!myPath.mkdirs()) System.out.println(myPath.getPath());

        OutputStream outS = new FileOutputStream(fop +"/" + str.trim());

        byte[] bytes = new byte[16 * 1024];
        int count;
        while ((count = in.read(bytes)) > 0) {
            outS.write(bytes, 0, count);
        }

        try {
            outS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void sendDataToHost() throws IOException {

        String name = "dracula.jpg";
        String client = "client1";
        //File file = new File("./client/src/main/file_storage/" + client + "/" + name); // server data
        File file = new File("./server/src/main/file_storage/" + client + "/" + name);
                try (
            InputStream in = new FileInputStream(file)){
            writeServiceDataToOut(Messages.POST_FILE,20);
            writeServiceDataToOut(name,100);
            writeDataToOut(in);
            out.flush();
        }

    }

    void getRequest(){

        try {
            writeServiceDataToOut(Messages.GET_FILE,20);
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
        while ((count = in.read(bytesData)) > 0) {
            out.write(bytesData, 0, count);
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

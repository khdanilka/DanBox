import java.io.*;
import java.net.Socket;


public class SocketThread extends Thread{

    private final Socket socket;
    private final SocketThreadListener eventListener;

    public SocketThread(SocketThreadListener eventListener,Socket socket){
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        System.out.println("Выписываем " + socket.isClosed());
        eventListener.readySocketClientThread(this);
        try (InputStream in = socket.getInputStream()) {

            while (!isInterrupted())
            {
                byte[] b = new byte[20];

                in.read(b, 0, b.length);
                String str = new String(b, "UTF-8").trim();

                //System.out.println("мы печатаем: " + str);
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
        String client = "client1";
        String fop ="./server/src/main/file_storage/" + client;
        File myPath = new File(fop);

        if (!myPath.mkdirs()) System.out.println(myPath.getPath());

        OutputStream out = new FileOutputStream(fop +"/" + str.trim());

        byte[] bytes = new byte[16 * 1024];
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void sendDataToHost() throws IOException {

        String name = "text.txt";
        String client = "client1";
        File file = new File("./client/src/main/file_storage/" + client + "/" + name);

        try (OutputStream out = socket.getOutputStream();
             InputStream in = new FileInputStream(file)){

            writeServiceDataToOut(out,Messages.POST_FILE,20);
            writeServiceDataToOut(out,name,100);

            writeDataToOut(out,in);
            out.flush();
        }

    }

    static void writeServiceDataToOut(OutputStream out, String name, int size) throws IOException {

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

    static void writeDataToOut(OutputStream out, InputStream in) throws IOException {
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

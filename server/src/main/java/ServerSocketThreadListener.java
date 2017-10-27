import java.net.Socket;


public interface ServerSocketThreadListener {
    void socketAccepted(Socket socket);
}

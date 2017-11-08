import java.io.UnsupportedEncodingException;

public class Messages {

    public static final String POST_FILE = "/post_f";
    public static final String GET_FILE = "/get_f";
    public static final String AUTH_REQUEST = "/auth_request";
    public static final String AUTH_ACCEPT = "/auth_accept";
    public static final String AUTH_ERROR = "/auth_error";

    public static final String DEL = "::";

    public static final int MESSAGE_SIZE = 100;

    public static void main(String[] args) throws UnsupportedEncodingException {


        byte[] b = Messages.messageAuthError("жопа");

        String str = new String(b, "UTF-8").trim();

        System.out.println(str);

    }

    public static byte[] messagePost(String nameOfFile, String fileSize){
        String name = POST_FILE + DEL + nameOfFile + DEL + fileSize;
        return createByteMessageFromString(name);
    }

    public static byte[] messageGet(String nameOfFile){
        String name = GET_FILE + DEL + nameOfFile;
        return createByteMessageFromString(name);

    }

    public static byte[] messageAuth(String login, String pass){
        String name = AUTH_REQUEST + DEL + login + DEL + pass;
        return createByteMessageFromString(name);
    }

    public static byte[] messageAuthAccepted(String login){
        String name = AUTH_ACCEPT + DEL + login;
        return createByteMessageFromString(name);
    }

    public static byte[] messageAuthError(String msg){
        String name = AUTH_ERROR + DEL + msg;
        return createByteMessageFromString(name);
    }


    private static byte[] createByteMessageFromString(String str){

        byte[] retBytes = initByteArray();
        byte[] nameB = returnBytesFromString(str);

        for(int i = 0; i <nameB.length; i++){
            retBytes[i] = nameB[i];
        }
        return retBytes;
    }

    private static byte[] initByteArray(){

        byte[] bytes = new byte[MESSAGE_SIZE];
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = 0;
        }
        return bytes;
    }

    private static byte[] returnBytesFromString(String str){

        byte[] nameB = new byte[0];
        try {
            nameB = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return nameB;

    }

}

import java.io.File;
import java.io.UnsupportedEncodingException;

public class Messages {

    public static final String POST_FILE = "/post_f";
    public static final String GET_FILE = "/get_f";
    public static final String AUTH_REQUEST = "/auth_request";
    public static final String AUTH_ACCEPT = "/auth_accept";
    public static final String AUTH_ERROR = "/auth_error";
    public static final String SUCCESS = "/success";
    public static final String GET_FILES_LIST = "/get_files_list";
    public static final String FILE_LIST = "/file_list";
    public static final String DELETE_FILE_FROM_SERVER = "/delete_file";
    public static final String FILE_DELETED = "/file_deleted";
    public static final String CLIENT_QUIT = "/quit";

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

    public static byte[] messageSuccess(){
        String name = SUCCESS;
        return createByteMessageFromString(name);
    }

    public static byte[] messageGetFilesList(){
        String name = GET_FILES_LIST;
        return createByteMessageFromString(name);
    }

//    public static byte[] messageSendFileList(File[] f){
//
//        StringBuffer stb = new StringBuffer().append(FILE_LIST);
//        if (f.length == 0) {
//            stb.append(DEL);
//            stb.append(0);
//            return createByteMessageFromString(String.valueOf(stb));
//        }
//        stb.append(DEL);
//        stb.append(1);
//
//        for (int i = 0; i < f.length; i++) {
//           if (f[i].isFile()) {
//               stb.append(DEL);
//               stb.append(f[i].getName());
//           }
//       }
//       System.out.println("мы это передаем в списке: " + String.valueOf(stb));
//       return returnBytesFromString(String.valueOf(stb));
//    }

    public static byte[] messageSendFileList(String sizeOfStr){
        String name = FILE_LIST + DEL + sizeOfStr;
        return createByteMessageFromString(String.valueOf(name));
    }

    public static byte[] messageDeleteFile(String nameOfFile){
        String name = DELETE_FILE_FROM_SERVER + DEL + nameOfFile;
        return createByteMessageFromString(String.valueOf(name));
    }

    public static byte[] messageDeleted(){
        String name = FILE_DELETED + DEL;
        return createByteMessageFromString(String.valueOf(name));
    }

    public static byte[] messageQuit(){
        String name = CLIENT_QUIT;
        return createByteMessageFromString(String.valueOf(name));
    }

    private static byte[] createByteMessageFromString(String str){

        byte[] retBytes = initByteArray();
        byte[] nameB = returnBytesFromString(str);

        for(int i = 0; i < nameB.length; i++){
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

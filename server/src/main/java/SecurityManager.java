/**
 * Created by android on 6/26/17.
 */
public interface SecurityManager {

    void init();
    String getNick(String login, String password);
    void dispose();
}

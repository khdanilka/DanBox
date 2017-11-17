import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements LoginNetworkManagerListener {


    ActionEvent actionEvent;

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtPass;

    ClientNetworkManager clNetwork = ClientNetworkManager.getClientNetworkManager();


    @FXML
    private void initialize() {
        clNetwork.setLogListener(this);
    }


    public void logIn(ActionEvent actionEvent) {
        String login = txtLogin.getText();
        String pass = txtPass.getText();
        clNetwork.auth_request(login,pass);
        this.actionEvent = actionEvent;
    }

    @Override
    public void authResponse(String message) {

        switch (message){
            case ClientNetworkManager.SUCCESS:
                javafx.scene.Node source = (javafx.scene.Node) actionEvent.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stageCloseOutThread(stage);
                //System.out.println("успешная авторизация");
                clNetwork.getClientListOfFilesWithPath();
                clNetwork.getServerFilesList();

                break;
            case ClientNetworkManager.ERROR:
                System.out.println("ошибка авторизации");

                break;
            default:
                throw new RuntimeException("неизвестный ответ на авторизацию " + message);
        }

    }

    private void stageCloseOutThread(Stage stage){
        Task<Void> t = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
        };
        t.setOnSucceeded(event -> {
            if (stage!= null)  stage.hide();
        });
        t.run();
    }
}

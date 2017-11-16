import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFXView extends Application{

    public static void main(String[] args) throws Exception {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "main2.fxml";
        //String fxmlFile = "login.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        stage.setTitle("JavaFX and Maven");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

        loginShow(stage);

    }


    private void loginShow(Stage stage){

        try {
            Stage stage1 = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage1.setTitle("Авторизация");
            stage1.setResizable(false);
            stage1.setScene(new Scene(root));
            stage1.initModality(Modality.WINDOW_MODAL);
            stage1.initOwner(stage.getScene().getWindow());
            stage1.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void stop() throws Exception
    {
        super.stop();

        ClientNetworkManager.getClientNetworkManager(null,null).buybuy();

        Platform.exit();
        System.exit(0);
    }


}
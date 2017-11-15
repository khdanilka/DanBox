
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientFXController implements ClientNetworkManagerListener {


    CollectionAddressBook m = new CollectionAddressBook();
    ClientNetworkManager clNetworkManager = ClientNetworkManager.getClientNetworkManager(this, null);

    @FXML
    private TableView client_list;

    @FXML
    private TableColumn<Person, String> columnInfo;


    @FXML
    private TableView server_list;

    @FXML
    private TableColumn<Person, String> columnServer;



    @FXML
    private void initialize() {

        m.fillTestData();
        columnInfo.setCellValueFactory(new PropertyValueFactory<>("fio"));

        columnServer.setCellValueFactory(new PropertyValueFactory<>("phone"));

        client_list.setItems(m.getPersonList());

        server_list.setItems(m.getPersonList());
    }



    public void logIn(ActionEvent actionEvent) {

        //clNetworkManager.auth_request();

    }


}

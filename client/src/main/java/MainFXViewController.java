
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MainFXViewController implements MainNetworkManagerListener {


    ClientNetworkManager clNetworkManager = ClientNetworkManager.getClientNetworkManager(this, null);

    @FXML
    private TableView client_list;

    @FXML
    private TableColumn<FileMain, String> columnInfo;


    @FXML
    private TableView server_list;

    @FXML
    private TableColumn<FileMain, String> columnServer;



    @FXML
    private void initialize() {

       // m.fillTestData();
        //columnInfo.setCellValueFactory(new PropertyValueFactory<>("fio"));

        //columnServer.setCellValueFactory(new PropertyValueFactory<>("phone"));

        //client_list.setItems(m.getPersonList());

        //server_list.setItems(m.getPersonList());
    }

    @FXML
    private void sendFiles(ActionEvent actionEvent){
        FileMain selectedFile = (FileMain) client_list.getSelectionModel().getSelectedItem();
        if ( selectedFile!= null) {
            clNetworkManager.addFileInSendRow(selectedFile.getFile());
            clNetworkManager.sendFilesToServer();
        }
    }

    @Override
    public void addingFileToServerResponse(String msg) {
        if (msg.equals(Messages.SUCCESS)){
            clNetworkManager.getServerFilesList();
        }
    }

    @FXML
    private void downloadFiles(ActionEvent actionEvent){
        FileMain selectedFile = (FileMain) server_list.getSelectionModel().getSelectedItem();
        if ( selectedFile!= null){
            clNetworkManager.addFileInGetRow(selectedFile.getFile());
            clNetworkManager.getFilesFromServer();
        }
    }

    @Override
    public void gettingFileFromServerResponse(String msg) {
        if (msg.equals(Messages.SUCCESS)){
            clNetworkManager.getClientListOfFilesWithPath();
        }
    }

    @FXML
    private void filePicker(ActionEvent actionEvent){

        FileChooser fileChooser = new FileChooser();
        javafx.scene.Node source = (javafx.scene.Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setTitle("FileMain Chooser Sample");
        java.io.File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            //openFile(file);
            System.out.println("мы выбрали файл " + file.getAbsolutePath());
            clNetworkManager.addFileToUsersDirectory(file.getAbsolutePath(),file.getName());
        }
    }


    private ObservableList<FileMain> clientFilesList;
    private ObservableList<FileMain> serverFilesList;

    @Override
    public void listClientsFiles(ArrayList<String> arrayList) {

        if(!arrayList.isEmpty()) {

            clientFilesList = FXCollections.observableList(getClientsFileFrom(arrayList));
            columnInfo.setCellValueFactory(new PropertyValueFactory<>("file"));
            client_list.setItems(clientFilesList);
        }

    }

    @Override
    public void listServerFiles(ArrayList<String> arrayList) {

        if(!arrayList.isEmpty()){
            serverFilesList = FXCollections.observableList(getClientsFileFrom(arrayList));
            columnServer.setCellValueFactory(new PropertyValueFactory<>("file"));
            server_list.setItems(serverFilesList);

        }
    }

    public class FileMain {
        private String file;
        public FileMain(String file) {
            this.file = file;
        }
        public String getFile() {
            return file;
        }
        public void setFile(String file) {
            this.file = file;
        }

    }

    public ArrayList<FileMain> getClientsFileFrom(ArrayList<String> str){
        ArrayList<FileMain> cl = new ArrayList<>();
        for(String s: str){
            cl.add(new FileMain(s));
        }
        return cl;
    }



}

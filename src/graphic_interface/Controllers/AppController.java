package graphic_interface.Controllers;

import graphic_interface.Alerts.AlertUtilities;
import graphic_interface.ShellThread;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import text_interface.TextInterface;
import permissions_management.*;
import web_management.WebManager;

import java.awt.*;
import java.io.IOException;



/*
* Funckje zaimplemwntowane
* CREATE FILE               logout
* DELETE FILE                addadmin
* OPEN FILE                    deleteUser
* RENAME FILE                   aadduser
* CLOSE FILE                    rename
* FILE LIST                     userinfo
*
* */

public class AppController {
    private MainController p;
    //przypisanie guzikow
    @FXML
    private Button _renameUser, _userInfo, _deleteUser, _logout, _createFile, _addUser,_readFile, _writeFile,_openFile,_closeFile,_accessList,
            _admin, _deleteFile, _fileList, _renameFile, _pKill, _pCreate, _webPing, _webNetstat, _webSend, _webListen, _usersInfo,
            _webOn, _webOff, _webSendFile, _copyFile, _copyRights, _moveRights, _deleteRights, _webHistory, _exit, _shareRights;


    @FXML
    private RadioButton _radioButton;

    @FXML
    private Label _pLabel, _usersLabel, _webLabel, _file1, _file2, _userNick;
    //inicjalizowanie pol
    @FXML
    void initialize() {
        _admin.setText("ADMIN");
        _addUser.setText("ADD USER");
        _renameUser.setText("RENAME USER");
        _userInfo.setText("USER INFO");
        _deleteUser.setText("DELETE USER");
        _logout.setText("LOGOUT");
        _createFile.setText("CREATE FILE");
        _deleteFile.setText("DELETE FILE");
        _fileList.setText("FILE LIST");
        _renameUser.setText("RENAME USER");
        _renameFile.setText("RENAME FILE");
        _pKill.setText("KILL");
        _pCreate.setText("CREATE");
        _usersInfo.setText("USERS INFO");
        _webListen.setText("LISTEN");
        _webSend.setText("SEND");
        _webNetstat.setText("NETSTAT");
        _webPing.setText("PING");
        _radioButton.setText(null);
        _writeFile.setText("WRITE FILE");
        _readFile.setText("READ FILE");
        _openFile.setText("OPEN FILE");
        _closeFile.setText("CLOSE FILE");
        _accessList.setText("ACCESS LIST");
        _webOn.setText("WEB ON");
        _webOff.setText("WEB OFF");
        _webSendFile.setText("SEND FILE");
        _copyFile.setText("COPY FILE");
        _exit.setText("EXIT");
        _shareRights.setText("SHARE RIGHTS");

        _copyRights.setText("COPY RIGHTS");
        _moveRights.setText("MOVE RIGHTS");
        _deleteRights.setText("DELETE RIGHTS");
        _webHistory.setText("WEB HISTORY");

        _pLabel.setText("PROCESSES");
        _file1.setText("FILES");
        _file2.setText("FILES");
        _webLabel.setText("WEB");
        _usersLabel.setText("USERS");
        _userNick.setText(UsersList.curr_user.get_name());
        _userNick.setStyle("-fx-text-fill: #2c8932");

        _radioButton.setDisable(true);
        _radioButton.getStyleClass().add("red-radio-button");
        _exit.setStyle("-fx-background-color: #933333");
        setFlags(true);



        AlertUtilities.sceneId=1;




    }
    //ustawienie mainkontrolera
    public void setMainController(MainController p) {
        this.p = p;
    }

    private void setFlags(boolean flag){
        _exit.setFocusTraversable(flag);
        _webHistory.setFocusTraversable(flag);
        _deleteRights.setFocusTraversable(flag);
        _moveRights.setFocusTraversable(flag);
        _copyRights.setFocusTraversable(flag);
        _copyFile.setFocusTraversable(flag);
        _webSendFile.setFocusTraversable(flag);
        _accessList.setFocusTraversable(flag);
        _closeFile.setFocusTraversable(flag);
        _openFile.setFocusTraversable(flag);
        _admin.setFocusTraversable(flag);
        _addUser.setFocusTraversable(flag);
        _renameUser.setFocusTraversable(flag);
        _userInfo.setFocusTraversable(flag);
        _deleteUser.setFocusTraversable(flag);
        _logout.setFocusTraversable(flag);
        _createFile.setFocusTraversable(flag);
        _deleteFile.setFocusTraversable(flag);
        _fileList.setFocusTraversable(flag);
        _renameUser.setFocusTraversable(flag);
        _renameFile.setFocusTraversable(flag);
        _pKill.setFocusTraversable(flag);
        _pCreate.setFocusTraversable(flag);
        _usersInfo.setFocusTraversable(flag);
        _webListen.setFocusTraversable(flag);
        _webSend.setFocusTraversable(flag);
        _webNetstat.setFocusTraversable(flag);
        _webPing.setFocusTraversable(flag);
        _writeFile.setFocusTraversable(flag);
        _readFile.setFocusTraversable(flag);;
        _webOff.setFocusTraversable(flag);
        _webOn.setFocusTraversable(flag);
        _shareRights.setFocusTraversable(flag);
    }

    @FXML
    public void logout() throws IOException {
        try {
            UsersList.logout();
            p.loadLoginScreen();

        } catch (Exception user_exception) {
            AlertUtilities.errorDialog(user_exception.toString());
        }

    }

    @FXML
    public void exit(){
        AlertUtilities.exitDialog();
    }

    @FXML
    public void shareRights(){
        AlertUtilities.shareRightsDialog();
    }
    @FXML
    public void moveRights(){
        AlertUtilities.moveRightDialog();
    }
    @FXML
    public void deleteRights(){
        AlertUtilities.deleteRightDialog();
    }
    @FXML
    public void copyRights(){
        AlertUtilities.copyRightDialog();
    }

    @FXML
    public void sendFile(){
        AlertUtilities.sendFileDialog();
    }
    @FXML
    public void readFile(){
        AlertUtilities.readFileDialog();
    }
    @FXML
    public void writeFile(){
        AlertUtilities.writeFileDialog();
    }

    @FXML
    public void pCreate(){
        AlertUtilities.pCreateDialog();
    }

    @FXML
    public void pKill(){
        AlertUtilities.pKillDialog();
    }
    @FXML
    public void radioSelect(){

    }

    @FXML
    public void webOn(){
        if(!WebManager.isOn){
            WebManager.NetOn();
            _radioButton.setSelected(true);
            AlertUtilities.showInfoDialog("Web is ON");
            return;
        }
        if(WebManager.isOn){
            AlertUtilities.errorDialog("Web is already on! Checkout the green dot above you");
            return;
        }
        else{
            AlertUtilities.errorDialog("Unknown error");
            return;
        }
    }


    @FXML
    public void webOff(){
        if(WebManager.isOn){
            _radioButton.setSelected(false);
            WebManager.NetOff();
            AlertUtilities.showInfoDialog("Web is OFF");
            return;
        }
        if(!WebManager.isOn){
            AlertUtilities.errorDialog("Web is already off! Checkout the green dot above you");
            return;
        }
        else{
            AlertUtilities.errorDialog("Unknown error");
            return;
        }

    }




    @FXML
    public void webHistory(){
        AlertUtilities.webHistoryDialog();
    }
    @FXML
    public void webPing(){
        AlertUtilities.pingDialog();
    }
    @FXML
    public void webNetstat(){
        AlertUtilities.netstatDialog();
    }
    @FXML
    public void webListen(){
        AlertUtilities.listenDialog();
    }
    @FXML
    public void webSend(){
        AlertUtilities.sendDialog();
    }
    //nadawanie statusu admina
    @FXML
    public void addAdmin() {
        AlertUtilities.addAdminDialog();
    }

    //dodwanie uzytkowanika
    @FXML
    public void addUser() {
        AlertUtilities.addUserDialog();
    }

    //usuwanie uzytkownika
    @FXML
    public void delete() {
        AlertUtilities.deleteUserDialog();
    }

    //zmiana nazwy uztkownika
    @FXML
    public void renameUser() {
        AlertUtilities.renameUserDialog();
    }

    //informacje o uzytkownikach
    @FXML
    public void userInfo() {
        AlertUtilities.userInfoDialog();
    }

    @FXML
    public void allUsersInfo(){
        AlertUtilities.allUserInfoDialog();
    }

    //tworzenie pliku - jakies problemy
    @FXML
    public void createFile() {
        AlertUtilities.createFileDialog();
    }

    //usuwanie pliku - powinno dzialac
    @FXML
    public void deleteFile() {
        AlertUtilities.deleteFileDialog();
    }

    //otwieranie pliku
    @FXML
    public void openFile() {
        AlertUtilities.openFileDialog();
    }

    //lista plikow
    @FXML
    public void fileList() {
        AlertUtilities.fileListDialog();
    }

    //zmiana nazwy pliku
    @FXML
    public void renameFile() {
        AlertUtilities.renameFileDialog();
    }

    //zamnikij plik
    @FXML
    public void closeFile() {
        AlertUtilities.closeFileDialog();
    }

    @FXML
    public void copyFile(){
        AlertUtilities.copyFileDialog();
    }
    //lista atrybutow
    @FXML
    public void accessList() {
        AlertUtilities.accesListDialog();
    }

    //pokazywanie danych
    @FXML
    public void showRegisters() {
        AlertUtilities.errorDialog("notimplemented");
    }

    @FXML
    public void showRam() {
        AlertUtilities.errorDialog("Sdasda");
    }



}

package graphic_interface.Controllers;

import graphic_interface.Alerts.AlertUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

import permissions_management.*;
import text_interface.TextInterface;


/*
cos nie tak z create user, funkcja add to list, zwraca po utworzeniu, ze cos istnieje
login dziala
create chyba tez
 */
public class LoginScreenController {


    private MainController mainController;

    @FXML
    private Button _login,_exit;

    @FXML
    private TextField _textFieldUserName;

    @FXML
    void initialize() {


        _login.setText("LOGIN");
        _exit.setText("EXIT");
        _login.setFocusTraversable(true);
        _exit.setFocusTraversable(true);
        _textFieldUserName.setFocusTraversable(true);
        _textFieldUserName.setPromptText("ADMIN");
        _textFieldUserName.setText("ADMIN");
        _textFieldUserName.setStyle("-fx-text-fill: black");
        _exit.setStyle("-fx-background-color: #933333");


        AlertUtilities.sceneId=0;

    }

    //ustawienie main kontrolera
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    //logowanie do menu glownego
    @FXML
    public void logToMenu() throws IOException {

        String user_name = _textFieldUserName.getText();
        if(UsersList.find_user(user_name) != -1){
            try {
                UsersList.login(user_name);
                createAppScreen();

            } catch (Exception user_exception) {
                AlertUtilities.errorDialog(user_exception.toString());
                return;
            }

        }
        else{
            AlertUtilities.errorDialog("User: " + user_name + " doesn't exists");
            return;
        }



    }

    private void createAppScreen() throws IOException {
        mainController.loadMainScreen();
    }

    @FXML
    public void exit(){
        AlertUtilities.exitDialog();
    }

    //tworzenie nowego użytkownika z poziomu login screena
//    @FXML
//    public void create() {
//        String user_name = _textFieldUserName.getText();
//        String choice = AlertUtilities.choiceDialog();
//
//        if(UsersList.find_user(user_name) == -1) {
//            if (choice.equals("Admin")) {
//                try {
//                    UsersList.add_user(user_name, true);
//                } catch (Exception UserException) {
//                    AlertUtilities.errorDialog(UserException.toString());
//                }
//                return;
//            }
//            if (choice.equals("User")) {
//                try {
//                    UsersList.add_user(user_name, false);
//                } catch (Exception UserException) {
//                    AlertUtilities.errorDialog(UserException.toString());
//                }
//                return;
//            }
//        }
//        else {
//            AlertUtilities.errorDialog("User: " + user_name +" already exists");
//            return;
//        }
//
//    }



}


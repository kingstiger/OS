package graphic_interface.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.WatchEvent;

public class MainController {


    @FXML
    private StackPane _loginScreenPane;

    @FXML
    public void initialize() throws IOException {
        loadMainScreen();
    }

    //ustawienie obrazu
    public void setScreen(Parent pane) {
        _loginScreenPane.getChildren().clear();
        _loginScreenPane.getChildren().add(pane);
    }

    //zaladowanie login screena
    @FXML
    public void loadLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/graphic_interface/XMLFiles/loginscreen.fxml"));
        Pane pane = loader.load();
        LoginScreenController loginScreenController = loader.getController();
        loginScreenController.setMainController(this);
        setScreen(pane);
    }


    public void loadMainScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/graphic_interface/XMLFiles/sample.fxml"));
        Pane pane = loader.load();
        AppController appController = loader.getController();
        appController.setMainController(this);
        setScreen(pane);
    }



}


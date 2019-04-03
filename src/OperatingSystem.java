import files_catalogs_management.*;
import graphic_interface.Alerts.AlertUtilities;
import graphic_interface.Controllers.MainController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import process_management.Pcb;
import process_management.PcbException;
import processor_management.SrtAlgorithm;
import ram_memory_management.RAM;
import permissions_management.*;
import text_interface.TextInterface;
import web_management.WebException;

import java.io.IOException;
import java.security.Key;

public class OperatingSystem extends Application
{

    private MainController m;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{


        RAM.ini();
        FilesCatalogsManager.generate();
        UsersList.init_users();
        SrtAlgorithm.ini();


        String test = "MVB49;MV[B]97;OUTA;JP25;A";

        String wyslij = "$ALOCATE$100$MVB50;ADB30;MV[45]B;STP\"A\"[45];";
        String odbierz = "$ALOCATE$100$RFP\"B\"[60];MVA[60];SBA70;OUTA;";

        String silnia = "MVA5;MVBA;DRA;MLBA;DRA;CMPA0;JNZ14;OUTB;";
        String selfChangingProgram = "MVA54;MVS0;MVB[A];OUTB;MV[S]B;IRS;IRA;CMPS5;JNZ11;JP0;JP59;";
        String fileTestProgram = "RFFA\"silnia\"3;CLF\"silnia\";OUTA;";

        String createprocess = "CP\"sm\"\"sm\";JP0;";

        String boobleSort = "MVA10;OF\"data\";MVA0;MVS60;RFFB\"data\"A;MV[S]B;IRA;IRS;CMPA6;JNZ20;SBSA;SBA6;MVB0;MVB[S];IRS;MVA[S];CMPBA;$ALOCATE$10$";

        String tabFile = "014235";

        String IP = "192.168.43.129";
        String webStart = "WO;WL\"1234\";" +
                "WQ\"" + IP + "\"\"rec\";" +
                "WQ\"" + IP + "\"\"snd\";" +
                "WQ\"" + IP + "\"\"file\";" +
                "WQ\"" + IP + "\"\"test\";" +
                "WQ\"" + IP + "\"\"search\";" +
                "WQ\"" + IP + "\"\"scp\";" +
                "WQ\"" + IP + "\"\"sort\";" +
                "WQ\"" + IP + "\"\"silnia\";" +
                "WQ\"" + IP + "\"\"sm\";" +
                "WQ\"" + IP + "\"\"data\";" +
                "WQ\"" + IP + "\"\"odbierz\";" +
                "WQ\"" + IP + "\"\"wyslij\";";

        String programSumFromFile = "JP5;\0OF\"data\";MVA0;ADB[4];OUT[4];OUTB;RFF[4]\"data\"A;IRA;CMPS1;JNZ19;OUTB;CLF\"data\";";

        String plik = "0";

        String test5 = "MVA10;OF\"data\";RFF[0]\"data\"0;OUTA;";


        String programSender = "MVA60;MV[A]A;IRA;CMPA100;JNZ6;STP\"A\"[60];$ALOCATE$200$";
        String programReceiver = "RFP\"B\"[20];OUTA;$ALOCATE$100$";



        try {
            FilesCatalogsManager.create("rec",programReceiver);
            FilesCatalogsManager.create("snd",programSender);
            FilesCatalogsManager.create("file",plik);
            FilesCatalogsManager.create("test",test5);
            FilesCatalogsManager.create("search",programSumFromFile);
            FilesCatalogsManager.create("scp",selfChangingProgram);
            FilesCatalogsManager.create("sort",boobleSort);
            FilesCatalogsManager.create("silnia",silnia);
            FilesCatalogsManager.create("sm",createprocess);
            FilesCatalogsManager.create("data",tabFile);
            FilesCatalogsManager.create("odbierz",odbierz);
            FilesCatalogsManager.create("wyslij",wyslij);
            FilesCatalogsManager.create("web", webStart);
        } catch (FileException e) {
            e.printStackTrace();
        }
        //FilesCatalogsManager.create("selfChanging",selfChangingProgram);
        //FilesCatalogsManager.create("test",test);
        try {
            FilesCatalogsManager.create("ft", fileTestProgram);
        } catch (FileException e) {
            e.printStackTrace();
        }

        Runnable runShell = ()->{
            try {
                TextInterface.run();
            } catch (Exception | PcbException | WebException | FileException e) {
                e.printStackTrace();
            }
        };

        Thread t = new Thread(runShell);
        t.start();

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/graphic_interface/XMLFiles/pane.fxml"));
        StackPane stackPane = loader.load();
        Scene scene = new Scene(stackPane);
        primaryStage.setScene( scene);
        primaryStage.setTitle("OS.7734");
        primaryStage.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();

        m=loader.getController();
        stackPane.setOnMousePressed( event-> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();

        });

        //move around here
        stackPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });


        stackPane.setOnMouseReleased(event -> {
            if(!AlertUtilities.logged.get()){
                try {
                    m.loadLoginScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(AlertUtilities.logged.get()){
                try {
                    m.loadMainScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        stackPane.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ESCAPE){
                AlertUtilities.exitDialog();
            }
        });
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}

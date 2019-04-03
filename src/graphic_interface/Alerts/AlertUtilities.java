package graphic_interface.Alerts;

import files_catalogs_management.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;
import process_management.PcbException;
import process_management.ProcessManager;
import permissions_management.*;
import web_management.WebException;
import web_management.WebManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlertUtilities {

    public static AtomicBoolean logged = new AtomicBoolean(true);
    public static int sceneId = 1;
    public static void showInfoDialog(String txt) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(txt);
        Stage s = (Stage) a.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));

        a.showAndWait();
    }

    public static void errorDialog(String error) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("ERROR");
        errorAlert.setHeaderText(error);
        Stage s = (Stage) errorAlert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));

        errorAlert.showAndWait();
    }

    public static String choiceDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choice Dialog");
        alert.setHeaderText("Look, a Confirmation Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("User");
        ButtonType buttonTypeTwo = new ButtonType("Admin");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
        String toReturn = "error";
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            toReturn = "User";
        }
        if (result.get() == buttonTypeTwo) {
            toReturn = "Admin";
        }
        if (result.get() == buttonTypeCancel) {
            alert.close();
        }

        return toReturn;
    }

    public static void infoDialog(String s, String title, String content, String lab) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Stage sc = (Stage) alert.getDialogPane().getScene().getWindow();
        sc.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Label label = new Label(lab);

        TextArea textArea = new TextArea(s);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);


        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static void renameUserDialog() {
        TextInputDialog dialog = new TextInputDialog("misiaczek");
        dialog.setTitle("RENAME USER");
        dialog.setHeaderText("Look, there is something to do");
        dialog.setContentText("Please enter new name:");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> UsersList.curr_user.set_name(name));
    }

    public static void addUserDialog() {
        TextInputDialog dialog = new TextInputDialog("misiaczek");
        dialog.setTitle("Add User");
        dialog.setHeaderText("Look, there is something to do");
        dialog.setContentText("Please enter new name:");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                UsersList.add_user(name, false);
            } catch (Exception user_exception) {
                AlertUtilities.errorDialog(user_exception.toString());
            }
        });

    }




    public static void addAdminDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Add Admin");
        alert.setHeaderText(null);
        alert.setContentText("Please, type user's name");

        Stage sc = (Stage) alert.getDialogPane().getScene().getWindow();
        sc.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Label label = new Label("List of users:");
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);


        String s = "";
        ArrayList<String> ar = UsersList.usersList();

        for (String z : ar) {
            s += z;
            s += System.lineSeparator();
        }

        TextArea textArea = new TextArea(s);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        TextField t = new TextField();
        t.setPromptText("Name");


        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(t, 0, 0);
        expContent.add(label, 0, 1);
        expContent.add(textArea, 0, 2);

        alert.getDialogPane().setContent(expContent);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            String user_name = t.getText();
            if (result.get() == ButtonType.OK) {
                if (UsersList.find_user(user_name) != -1) {
                    try {
                        if (UsersList.users.get(UsersList.find_user(user_name)).is_admin())
                        {
                            UsersList.set_normal_mode(user_name);
                        } else
                            UsersList.set_admin_mode(user_name);
                    } catch (Exception user_exception) {
                        AlertUtilities.errorDialog(user_exception.toString());
                    }
                    return;
                } else {
                    AlertUtilities.errorDialog("User: " + user_name + " doesn't exists");
                    return;
                }
            }

            if (result.get() == ButtonType.CANCEL) {
                alert.close();
                return;
            }
        }

    }

    public static void deleteUserDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Delete User");
        alert.setHeaderText(null);
        alert.setContentText("Please, type user's name");
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        Stage sc = (Stage) alert.getDialogPane().getScene().getWindow();
        sc.getIcons().add(new Image("/graphic_interface/Style/icon.png"));

        Label label = new Label("List of users:");

        String s = "";
        ArrayList<String> ar = UsersList.usersList();

        for (String z : ar) {
            s += z;
            s += System.lineSeparator();
        }

        TextArea textArea = new TextArea(s);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        TextField t = new TextField();
        t.setPromptText("Name");


        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(t, 0, 0);
        expContent.add(label, 0, 1);
        expContent.add(textArea, 0, 2);

        alert.getDialogPane().setContent(expContent);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            String user_name = t.getText();
            if (result.get() == ButtonType.OK) {
                if (UsersList.find_user(user_name) != -1) {
                    try {
                        UsersList.delete_user(user_name);
                    } catch (Exception user_exception) {
                        AlertUtilities.errorDialog(user_exception.toString());
                    }
                    return;
                } else {
                    AlertUtilities.errorDialog("User: " + user_name + " doesn't exists");
                    return;
                }
            }

            if (result.get() == ButtonType.CANCEL) {
                alert.close();
                return;
            }
        }
    }

    public static void userInfoDialog() {
        String s = UsersList.userInfo();
        AlertUtilities.infoDialog(s, "User Info", "This is user info", "User: ");
    }

    public static void allUserInfoDialog() {
        String s = UsersList.allUserInfo();
        AlertUtilities.infoDialog(s, "Users List", "This is users list", "Users list: ");
    }

    public static void createFileDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Create File");
        dialog.setHeaderText("Please, enter details");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("CANCEL", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(loginButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("Name");
        TextField to = new TextField();
        to.setPromptText("Content");
        Label name = new Label();
        name.setText("File's name");
        Label content = new Label();
        content.setText("Content");
        gridPane.add(name, 0, 0);
        gridPane.add(from, 0, 1);
        gridPane.add(content, 0, 2);
        gridPane.add(to, 0, 3);

        dialog.getDialogPane().setContent(gridPane);

        from.requestFocus();
        to.requestFocus();
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            String filename = from.getText();
            String filecontent = to.getText();

            if (result.get() == loginButtonType) {
                //jesli plik o nazwie nie istneije i jest pusty
                if (FilesCatalogsManager.check_file_exist(filename) == false
                        && filecontent.equals("")) {
                    try {
                        FilesCatalogsManager.create(filename);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                //jesli plik o nazwie nie iestnieje i jest pelny
                if (FilesCatalogsManager.check_file_exist(filename) == false
                        && !filecontent.equals("")) {
                    try {
                        FilesCatalogsManager.create(filename, filecontent);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }

                //jesli pole nazwy jest puste
                if (filename.equals("") || filename.equals(null) || filename.isEmpty()) {
                    AlertUtilities.errorDialog("You didn't type a file's name");
                    return;
                }

                //jesli plik juz istnieje
                if (FilesCatalogsManager.check_file_exist(filename) == true) {
                    AlertUtilities.errorDialog("File: " + filename + " already exists");
                    return;
                } else {
                    AlertUtilities.errorDialog("Unknown error");
                }
            }
            if (result.get() == cancelButtonType) {
                dialog.close();
            }

        }

    }

    public static void deleteFileDialog() {
        TextInputDialog dialog = new TextInputDialog("File name");
        dialog.setTitle("DELETE FILE");
        dialog.setHeaderText("Look, there is something to do");
        dialog.setContentText("Please enter file's name:");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Optional<String> result = dialog.showAndWait();


        if (result.isPresent()) {

            String name = result.get();


            try {
                FilesCatalogsManager.delete_file(name);
            } catch (FileException ex) {
                AlertUtilities.errorDialog(ex.toString());
                return;
            }


        }
    }

    public static void openFileDialog() {
        TextInputDialog dialog = new TextInputDialog("File name");
        dialog.setTitle("OPEN FILE");
        dialog.setHeaderText("Look, there is something to do");
        dialog.setContentText("Please enter file's name:");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Optional<String> result = dialog.showAndWait();


        if (result.isPresent()) {

            String name = result.get();

            try {
                FilesCatalogsManager.open_file(name);
            } catch (FileException ex) {
                AlertUtilities.errorDialog(ex.toString());
            }

        }
    }

    public static void renameFileDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Rename File");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("Previous name");
        TextField to = new TextField();
        to.setPromptText("New name");

        gridPane.add(from, 0, 0);
        gridPane.add(new Label("To:"), 1, 0);
        gridPane.add(to, 2, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(() -> from.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(from.getText(), to.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            String filename = result.get().getKey();
            String newfilename = result.get().getValue();

            try {
                FilesCatalogsManager.rename_file(filename, newfilename);
            } catch (FileException ex) {
                AlertUtilities.errorDialog(ex.toString());
            }


        }

    }


    public static void closeFileDialog() {
        TextInputDialog dialog = new TextInputDialog("File's name");
        dialog.setTitle("Close File");
        dialog.setHeaderText("Look, there is something to do");
        dialog.setContentText("Please enter file's name:");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String filename = result.get();

            try {
                FilesCatalogsManager.close_file(filename);
            } catch (FileException ex) {
                AlertUtilities.errorDialog(ex.toString());
            }
        }
    }

    public static void fileListDialog() {
        List<String> list = FilesCatalogsManager.list_of_files();
        String s = "";
        for (String z : list) {
            s += z;
            s += System.lineSeparator();
        }
        AlertUtilities.infoDialog(s, "File List", "This is file list", "File list: ");
    }


    public static void accesListDialog() {

        if(FilesCatalogsManager.list_of_files()!=null){
            String s = createAtri();
            AlertUtilities.infoDialog(s, "Access List","This is access list", "Access list" );
        }
        else{
            AlertUtilities.errorDialog("List is empty");
        }
    }

    public static void pCreateDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Process Create");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Type everything needed.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField content = new TextField();
        content.setPromptText("Content");



        gridPane.add(name, 0, 0);
        gridPane.add(content, 1, 0);


        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent()) {
            String filename = name.getText();
            String contentText = content.getText();
            if (result.get() == buttonTypeOne) {
                try {
                    ProcessManager.createProcess(contentText, filename);
                    return;
                } catch (PcbException e) {
                    AlertUtilities.errorDialog(e.toString());
                    return;
                }
            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }
        }


    }



    public static void pKillDialog() {
        TextInputDialog dialog = new TextInputDialog("Proccess name");
        dialog.setTitle("Kill Proccess");
        dialog.setHeaderText("Look, there is something to do");
        dialog.setContentText("Please enter proccess's name:");
        Stage s = (Stage) dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        Optional<String> result = dialog.showAndWait();


        if (result.isPresent()) {
            String pName = result.get();
            try {
                ProcessManager.terminateProcess(pName);
                return;
            } catch (PcbException e) {
                AlertUtilities.errorDialog(e.toString());
                return;
            }
        }

    }


    public static void writeFileDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Write File");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("File name");
        TextField to = new TextField();
        to.setPromptText("Data");

        gridPane.add(from, 0, 0);
        gridPane.add(to, 2, 0);


        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent()) {
            String txt1 = from.getText();
            String txt2 = to.getText();
            if (result.get() == buttonTypeOne) {
                try {
                    FilesCatalogsManager.write_file_string_end(txt1, txt2);
                } catch (FileException e) {
                    AlertUtilities.errorDialog(e.toString());
                }
                return;
            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }
        }
    }


    public static void readFileDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Read File");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("Name");


        gridPane.add(from, 0, 0);


        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            String filename = from.getText();
            if (result.get() == buttonTypeOne) {
                if (FilesCatalogsManager.check_file_exist(filename)) {
                    try {
                        infoDialog(FilesCatalogsManager.read_all_file(filename), "Read File", "Show more", "File");
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                } else {
                    AlertUtilities.errorDialog("Wrong! File: " + filename + " doesn't exists");
                    return;
                }
            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }

        }
    }


    public static void listenDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Listen on Port");
        alert.setHeaderText("Look, there is something to do!");
        alert.setContentText("Type port number");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("Name");
        from.setText("1234");

        gridPane.add(from, 0, 0);


        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent()) {
            String port = from.getText();
            if (result.get() == buttonTypeOne) {
                if (WebManager.isOn == true) {
                    WebManager.Listen(Integer.parseInt(port));
                }
                if (WebManager.isOn == false) {
                    AlertUtilities.errorDialog("Web is already off! Checkout your connection!");
                }
                return;
            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }
        }
    }


    public static void sendDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Web Send");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Type everything needed.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField ip = new TextField();
        ip.setPromptText("IP");
        ip.setText("127.0.0.1");
        TextField port = new TextField();
        port.setPromptText("Port");
        port.setText("27015");
        TextField text = new TextField();
        text.setPromptText("TEXT");


        gridPane.add(ip, 0, 0);
        gridPane.add(port, 1, 0);
        gridPane.add(text, 2, 0);


        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            String ipad = ip.getText();
            String portad = port.getText();
            String msg = text.getText();

            if (result.get() == buttonTypeOne) {
                if (WebManager.isOn) {
                    if(msg.equals("") || msg.equals(null) || msg.isEmpty()){
                        AlertUtilities.errorDialog("You should've typed text!");
                    }
                    else {
                        try {
                            WebManager.Send(ipad, Integer.parseInt(portad), msg);
                        } catch (WebException e) {
                            AlertUtilities.errorDialog(e.getMessage());
                        }
                        return;
                    }
                }
                    else {
                    AlertUtilities.errorDialog("Net is off!");
                    return;
                }

            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }

        }


    }


    public static void pingDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ping");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Type IP address");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("IP");
        from.setText("127.0.0.1");


        gridPane.add(from, 0, 0);


        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            String ip = from.getText();
            if (result.get() == buttonTypeOne) {
                try {
                    WebManager.Ping(ip);
                } catch (WebException e) {
                    AlertUtilities.errorDialog(e.getMessage());
                }
                return;
            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }
        }

    }

    public static void sendFileDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Web Send File");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Type everything needed.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField ip = new TextField();
        ip.setPromptText("IP");
        ip.setText("127.0.0.1");
        TextField text = new TextField();
        text.setPromptText("File Name");


        gridPane.add(ip, 0, 0);
        gridPane.add(text, 1, 0);


        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            String ipad = ip.getText();
            String filename = text.getText();

            if (result.get() == buttonTypeOne) {
                if (WebManager.isOn) {
                    if(FilesCatalogsManager.check_file_exist(filename))
                    {

                        try {
                            try {
                                WebManager.sendFile(ipad, filename);
                            } catch (WebException e) {
                                AlertUtilities.errorDialog(e.getMessage());
                            }
                            return;
                        } catch (FileException e) {
                            AlertUtilities.errorDialog(e.toString());
                            return;
                        }
                    }
                    if(!FilesCatalogsManager.check_file_exist(filename)){
                        AlertUtilities.errorDialog("File: " + filename + " doesn't exists");
                        return;
                    }
                } else {
                    AlertUtilities.errorDialog("Net is off!");
                    return;
                }

            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }

        }
    }


    public static void copyFileDialog(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Copy File");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("Copied file");
        TextField to = new TextField();
        to.setPromptText("New name");

        gridPane.add(from, 0, 0);
        gridPane.add(to, 2, 0);


        alert.getDialogPane().setContent(gridPane);


        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent()) {
            String copiedfile = from.getText();
            String newname = to.getText();
            if (result.get() == buttonTypeOne) {
                if (FilesCatalogsManager.check_file_exist(copiedfile) && !FilesCatalogsManager.check_file_exist(newname)) {
                    try {
                        String msg = FilesCatalogsManager.read_all_file(copiedfile);
                        FilesCatalogsManager.create(newname, msg);
                        return;
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                        return;
                    }
                }
                if(!FilesCatalogsManager.check_file_exist(copiedfile)){
                    AlertUtilities.errorDialog("File: " + copiedfile + " doesn't exist");
                    return;
                }
                if(FilesCatalogsManager.check_file_exist(newname)){
                    AlertUtilities.errorDialog("File: " + newname + " already exists");
                    return;
                }
            }
            if (result.get() == buttonTypeCancel) {
                alert.close();
                return;
            }
        }
    }

    public static String createAtri(){
        String toReturn="";
        for (String str: FilesCatalogsManager.files_rights()) {
            toReturn += str;
        }
        return toReturn;
    }

    public static void copyRightDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Copy Rights");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        TextField from = new TextField();
        from.setPromptText("File Name");
        TextField to = new TextField();
        to.setPromptText("User name");

        CheckBox ch1 = new CheckBox("EXECUTE");
        CheckBox ch2 = new CheckBox("READ");
        CheckBox ch3 = new CheckBox("WRITE");

        gridPane.add(from, 0, 0);
        gridPane.add(to, 2, 0);
        gridPane.add(ch1, 0, 1);
        gridPane.add(ch2, 0, 2);
        gridPane.add(ch3, 0, 3);

        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){

            String fileName = from.getText();
            String userName = to.getText();

            if(result.get() == buttonTypeOne){
                if(ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch3.isSelected() && !ch2.isSelected()){
                    try {
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch2.isSelected() && ch3.isSelected() && !ch1.isSelected()){
                    try {
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.READ);
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.READ);
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.WRITE);
                        FilesCatalogsManager.copyright(fileName, userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    AlertUtilities.errorDialog("You didn't select any option");
                    return;
                }


            }

            if(result.get()==buttonTypeCancel){
                alert.close();
            }
        }
    }

    public static void moveRightDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Move Rights");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        TextField from = new TextField();
        from.setPromptText("File Name");
        TextField to = new TextField();
        to.setPromptText("User name");

        CheckBox ch1 = new CheckBox("EXECUTE");
        CheckBox ch2 = new CheckBox("READ");
        CheckBox ch3 = new CheckBox("WRITE");

        gridPane.add(from, 0, 0);
        gridPane.add(to, 2, 0);
        gridPane.add(ch1, 0, 1);
        gridPane.add(ch2, 0, 2);
        gridPane.add(ch3, 0, 3);

        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){

            String fileName = from.getText();
            String userName = to.getText();
            String curUser = UsersList.curr_user.get_name();
            if(result.get() == buttonTypeOne){
                if(ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.moveright(fileName,  userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.moveright(fileName, userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.moveright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.moveright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.moveright(fileName,  userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch3.isSelected() &&  !ch2.isSelected()){
                    try {
                        FilesCatalogsManager.moveright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.moveright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch2.isSelected() && ch3.isSelected() && !ch1.isSelected()){
                    try {
                        FilesCatalogsManager.moveright(fileName, userName, AccessMode.READ);
                        FilesCatalogsManager.moveright(fileName,userName,  AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.moveright(fileName,userName,AccessMode.READ);
                        FilesCatalogsManager.moveright(fileName, userName, AccessMode.WRITE);
                        FilesCatalogsManager.moveright(fileName,userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    AlertUtilities.errorDialog("You didn't select any option");
                    return;
                }


            }

            if(result.get()==buttonTypeCancel){
                alert.close();
            }
        }
    }

    public static void deleteRightDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete Rights");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        TextField from = new TextField();
        from.setPromptText("File Name");
        TextField to = new TextField();
        to.setPromptText("User name");

        CheckBox ch1 = new CheckBox("EXECUTE");
        CheckBox ch2 = new CheckBox("READ");
        CheckBox ch3 = new CheckBox("WRITE");

        gridPane.add(from, 0, 0);
        gridPane.add(to, 2, 0);
        gridPane.add(ch1, 0, 1);
        gridPane.add(ch2, 0, 2);
        gridPane.add(ch3, 0, 3);

        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()){

            String fileName = from.getText();
            String userName = to.getText();

            if(result.get() == buttonTypeOne){
                if(ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    } catch (PermissionException permission_exception) {
                        AlertUtilities.errorDialog(permission_exception.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    } catch (PermissionException permission_exception) {
                       AlertUtilities.errorDialog(permission_exception.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    } catch (PermissionException permission_exception) {
                        AlertUtilities.errorDialog(permission_exception.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    } catch (PermissionException permission_exception) {
                        AlertUtilities.errorDialog(permission_exception.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch3.isSelected() && !ch2.isSelected()){
                    try {
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    } catch (PermissionException permission_exception) {
                        AlertUtilities.errorDialog(permission_exception.toString());
                    }
                    return;
                }
                if(ch2.isSelected() && ch3.isSelected() && !ch1.isSelected()){
                    try {
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.READ);
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    } catch (PermissionException permission_exception) {
                        AlertUtilities.errorDialog(permission_exception.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.READ);
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.WRITE);
                        FilesCatalogsManager.deleteright(fileName, userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    } catch (PermissionException permission_exception) {
                        AlertUtilities.errorDialog(permission_exception.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    AlertUtilities.errorDialog("You didn't select any option");
                    return;
                }


            }

            if(result.get()==buttonTypeCancel){
                alert.close();
            }
        }
    }

    public static void webHistoryDialog(){
        String s = WebManager.getAllHistory();
        AlertUtilities.infoDialog(s, "Web History", "Show more", "History");
    }

    public static void exitDialog(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("EXIT");
        alert.setHeaderText("Do you really want to close system?");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeYes = new ButtonType("YES");
        ButtonType buttonTypeNo = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));



        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){
            if(result.get()==buttonTypeYes){
                System.exit(0);
                return;
            }
            if(result.get()==buttonTypeNo){
                alert.close();
                return;
            }
        }
    }

    public static void netstatDialog(){
        String s = WebManager.Netstat();
        AlertUtilities.infoDialog(s, "Netstat", "Show more", "Netstat");
    }

    public static void shareRightsDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Share Rights");
        alert.setHeaderText("Look, a Dialog with Custom Actions");
        alert.setContentText("Choose your option.");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("/graphic_interface/Style/icon.png"));
        ButtonType buttonTypeOne = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        TextField from = new TextField();
        from.setPromptText("File Name");
        TextField to = new TextField();
        to.setPromptText("User name");

        CheckBox ch1 = new CheckBox("EXECUTE");
        CheckBox ch2 = new CheckBox("READ");
        CheckBox ch3 = new CheckBox("WRITE");

        gridPane.add(from, 0, 0);
        gridPane.add(to, 2, 0);
        gridPane.add(ch1, 0, 1);
        gridPane.add(ch2, 0, 2);
        gridPane.add(ch3, 0, 3);

        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){

            String fileName = from.getText();
            String userName = to.getText();

            if(result.get() == buttonTypeOne){
                if(ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && !ch3.isSelected()){
                    try {
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.READ);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch3.isSelected() && !ch2.isSelected()){
                    try {
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.EXECUTE);
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch2.isSelected() && ch3.isSelected() && !ch1.isSelected()){
                    try {
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.READ);
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.WRITE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(ch1.isSelected() && ch2.isSelected() && ch3.isSelected()){
                    try {
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.READ);
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.WRITE);
                        FilesCatalogsManager.shareright(fileName, userName, AccessMode.EXECUTE);
                    } catch (FileException e) {
                        AlertUtilities.errorDialog(e.toString());
                    }
                    return;
                }
                if(!ch1.isSelected() && !ch2.isSelected() && !ch3.isSelected()){
                    AlertUtilities.errorDialog("You didn't select any option");
                    return;
                }


            }

            if(result.get()==buttonTypeCancel){
                alert.close();
            }
        }
    }
}


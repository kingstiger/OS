package web_management;
import files_catalogs_management.FileException;
import files_catalogs_management.FilesCatalogsManager;
import permissions_management.UsersList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class WebManager
{
    static ServerSocket server_socket;
    public static Socket client_socket;
    public static List<Client> connectedClients = new ArrayList<>();
    public static List<History> historyList = new ArrayList<>();
    static Thread thread;
    public static boolean isOn; //flaga informująca, czy siec (nasłuchiwanie) jest włączone, infrmuje, czy mozan wysylac
    //Map<int, PCB> map;
    //mapa int listaPCB

    public WebManager() {
        isOn = false;
    }

    //WEBON - interpreter/textowy - włącza obsługę sieci
    public static void NetOn(){
        if(UsersList.curr_user==null)
            throw new NullPointerException("User is logged out");

        if(!isOn){
            System.out.println("[WEB]: Network turned on");
            isOn = true; //ustawia flagę
        }
        else
            System.out.println("[WEB]: Network was turned on");


    }

    //Zaczyna nasluchiwanie na konkretnym PORCIE
    //LISTEN PORT - interpreter/teksotwy
    public static void Listen(int PORT){
        if(UsersList.curr_user==null)
            throw new NullPointerException("User is logged out");

        System.out.println("[WEB]: Listening on port " + PORT);
        //ustanawia połączenie
        try{
            server_socket = new ServerSocket(PORT);
            thread = new Thread(new ReceiveManager(server_socket));
            thread.start();
        }
        catch (Exception e){
            System.out.println("Port already used");
        }
    }

    //WEBOFF - interpreter/tekstowy - wyłącza obsługę sieci
    public static void NetOff(){
        if(UsersList.curr_user==null)
            throw new NullPointerException("User is logged out");

        System.out.println("[WEB]: Network turned off");
        isOn = false;
    }

    public static boolean stepWork(String[] command)
    {
        if(command[0].equals("WEB")){
            if(command.length == 2) {
                //DRUKOWANIE STANU
                if (command[1].equals("STATE")) {
                    if (isOn)
                        System.out.println("[WEB]: Network is available");
                    else
                        System.out.println("[WEB]: Network is unavailable");

                }

                //DRUKOWANIE HISTORII
                else if (command[1].equals("HISTORY")) {
                    for (History his : historyList) {
                        if (his.getIsSend())
                            System.out.println("[WEB]: Sent to: " + his.getClient().IP + ":" + his.getClient().PORT + " Message: " + his.getText());

                        else
                            System.out.println("[WEB]: Received from: " + his.getClient().IP + ":" + his.getClient().PORT + " Message: " + his.getText());

                    }
                } else {
                    System.out.println("[WEB]: Invalid arguments");
                }
            } else {
                System.out.println("[WEB]: Invalid arguments lenght");
            }
            return true;
        }
        return false;
    }

    //udostpnianie plikow
    //
    //WEBSEND IP PORT OBJ - interpreter/tekstowy - do wysyłania do konkretnego odbiorcy
    public static void Send(String IP, int PORT, String text) throws WebException{
        if(UsersList.curr_user==null)
            throw new NullPointerException("User is logged out");

        if (text.equals("") || text == null) {
            throw new WebException("[WEB]: Noting to send");
        }

        if(isOn){
            //historia operacji
            History history =
                    new History(new Client(PORT, IP), text, true);
            historyList.add(history);
            //koniec historii

            try {
                client_socket = new Socket(IP, PORT);
            } catch (Exception e) {
                throw new WebException("[WEB]: Problem with sending. This port is used or client is not in network");
            }
            System.out.println("[WEB]: I'm sending sth");
            PrintWriter out;
            try {
                out = new PrintWriter(client_socket.getOutputStream(), true);
                out.println(text);
            } catch (Exception e) {
                System.out.println("[WEB]: Problem with arguments in Send Method. Try again\nIt should looks like: WEBSEND 127.0.0.1 8080 sthYouWantToSend");
            }
        }
        else{
            System.out.println("[WEB]: Cannot send. Network is disabled");
        }
        try {
            client_socket.close();
        } catch (Exception e) {
            System.out.println("[WEB]: Probably invalid arguments. It should looks like: WEBSEND 127.0.0.1 8080 sthYouWantToSend");
        }
    }

    //PING IP - interpreter/tekstowy - do wysyłania do konkretnego odbiorcy
    public static void Ping(String ip) throws WebException{
        if(UsersList.curr_user==null)
            throw new NullPointerException("User is logged out");

        if(isOn){
            //String ip = "127.0.0.1";
            String pingResult = "";

            String pingCmd = "ping " + ip;
            try {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(pingCmd);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(p.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    pingResult += inputLine;
                }
                in.close();

            } catch (Exception e) {
                System.out.println(e);
            }

        }
        else
            throw new WebException("Web is Off");

    }

    public static void sendFile(String IP, String fileName) throws WebException, FileException {
        FilesCatalogsManager.open_file(fileName);
        String toSend = "FILESEND_" + fileName + ":" + FilesCatalogsManager.read_all_file(fileName);;
        Send(IP, 1234, toSend);
        FilesCatalogsManager.close_file(fileName);
    }

    //NETSTAT lub
    //PRNTCLIENTS - interpreter/tekstowy - wyświetla podłączonych klientów
    public static String Netstat(){
        if(UsersList.curr_user==null)
            throw new NullPointerException("User is logged out");

        String toReturn = "";
        int licznik = 0;
        System.out.println("[WEB]: Connected clients: ");
        toReturn += "Connected clients: \n";
        for(Client cl : connectedClients){
            toReturn = "Client: " + "IP: " + cl.IP + "PORT: " + cl.PORT;
            System.out.println("Client: " +
                    "IP: " + cl.IP +
                    "PORT: " + cl.PORT);
        }
        return toReturn;
    }

    public static void AddCl(Client cl){
        boolean isFound = false;
        //sprawdza czy jest taki klient
        for(Client client : connectedClients){
            if(client.IP.equals(cl.IP) && client.PORT == cl.PORT)
            {
                System.out.println("[WEB]: Client found");
                isFound = true;
            }

        }
        if(isFound == false) {

            connectedClients.add(cl);
            System.out.println("[WEB]: New client connected. IP: " +
                    cl.IP +
                    " PORT: " + cl.PORT
            );

        }
    }

    public static String getAllHistory(){
        String history = "";
        for (History his : historyList) {
            if (his.getIsSend())
                history += "Sent to: " + his.getClient().IP + ":" + his.getClient().PORT + " Message: " + his.getText() + "\n";

            else
                history += "Received from: " + his.getClient().IP + ":" + his.getClient().PORT + " Message: " + his.getText() + "\n";

        }
        //System.out.println(history);
        return history;
    }
}


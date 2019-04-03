package web_management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import files_catalogs_management.*;


public class ReceiveManager extends Thread {
    //PORTY DO KONKRETNYCH ZADAN
    final int PORT_FILE = 1234; //PORT DO PRZESYLANIA INFORMACJI O PLIKACH
    final int PORT_MATH = 1235; //PORT DO OPERACJI MATEMATYCZNYCH
    ServerSocket server_socket;
    Socket client_socket;
    int PORT;

    public ReceiveManager(ServerSocket server_socket){
        this.server_socket = server_socket;
        this.PORT = server_socket.getLocalPort();
    }


    @Override
    public void run(){
        while (WebManager.isOn)
        {
            try {
                client_socket = server_socket.accept(); //AKCEPTUJE I PRZYPISUJE DO CLIENT_SOCKET DANE O KLIENCIE

                Client cl = new Client(client_socket.getLocalPort(), client_socket.getInetAddress()); //TWORZY INFORMACJE O PODLACZONYM KLIENCIE
                WebManager.AddCl(cl); //DODAJE KLIENTA DO LISTY KLIENTOW

                //POCZATEK ODBIERANIA
                String answer; //ODPOWEIDZ OD SERWERA
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(client_socket.getInputStream())); //TWORZY OBIEKT DO CZYTANIA DANYCH
                answer = input.readLine(); //czyta to co odebrał

                if(answer != null && WebManager.isOn){ //JEZELI ODPOWIEDZ NIE JEST NULLEM
                    System.out.println("[WEB]: Answer: " + answer + " S: " + client_socket.getLocalPort() + " " + client_socket.getInetAddress()); //DRUKUJE INFORMACJE O KLIENTCIE
                }
                //KONIEC ODBIERANIA

                String ToSend = RequestManagement(answer, client_socket); //ZWRACA COS DO WYSLANIA. REQUESTMANAGEMENT USTANAWIA CO WYSLAC
                //System.out.println("To send:" + ToSend);

                //POCZATEK WYSYLANIA
                if(ToSend != null && !ToSend.equals("")){
                    //odesłanie czegoś
                    PrintWriter out = null;

                    String IP = client_socket.getInetAddress().toString(); //POBIERA ADRES IP SERWERA
                    IP = IP.replace("/", ""); //WYRZUCA '/' Z ADRESU IP, BO JEST SMIECIOWE TUTAJ


                    WebManager.Send(IP, client_socket.getLocalPort(), ToSend); //WYSYLA ZA POMOCA WEBMANAGEMENT

                    System.out.println("[WEB]: Message sent");

                }
                else throw new WebException("[WEB]: Nothing to send... Message is null");
                //KONIEC ODSYLANIA

            } catch (IOException e) {
                e.printStackTrace();
            } catch (WebException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //wrzucanie do RAMU
    //do zarządzania odebranymi danymi - jak odebrany komunikat, no to obsługuje go
    //tj. to przekierowuje do odpowiednich metod w zaleznosci od portu na ktorym to jest wysylane
    private String RequestManagement(String req, Socket socket){
        if(WebManager.isOn){
            //dodawanie do historii
            History history =
                    new History(new Client(PORT, client_socket.getInetAddress()), req,false);
            WebManager.historyList.add(history);
            //koniec dodawania do historii

            //MUSI BYC EQUALS
            String answer = null;

            switch (socket.getLocalPort()){
                case PORT_FILE:
                    answer = FileOP(req);
                    break;

                case PORT_MATH:
                    answer = MathOP(req);
                    break;

                default:
                    System.out.println("[WEB]: Port problem? Port: " + this.PORT);
                    break;
            }
            return answer;
        }
        else return null;
    }

    //oblsuguje port 1234
    private String FileOP(String req) {
        final String FILES_REQ = "FILESREQ"; //jesli wyslemy takie żądanie, to odsyła listę plików
        final String FILE_REQ = "FILEREQ_"; //odsyła konkretny plik: format żądania: FILEREQ_nazwapliku
        final String FILE_SND = "FILESEND_"; //odsyła konkretny plik: format żądania: FILEREQ_nazwapliku

        if(WebManager.isOn){
            if(req.equals(FILES_REQ)){
                //System.out.println(req);
                //System.out.println(PORT);
                String files = FilesCatalogsManager.list_of_files().toString();
                return files;
            }
            else if(req.contains(FILE_REQ)){
                //System.out.println(req);
                //System.out.println(PORT);
                req = req.replace(FILE_REQ, "");

                try {
                    FilesCatalogsManager.open_file(req);
                    String file = null;
                    file = FilesCatalogsManager.read_all_file(req);
                    FilesCatalogsManager.close_file(req);
                    return FILE_SND + req + ":" + file;

                } catch (FileException e) {
                    e.printStackTrace();
                }
            }

            else if(req.contains(FILE_SND)){
                req = req.replace(FILE_SND, "");
                String[] plik = req.split(":");

                try {
                    FilesCatalogsManager.create(plik[0], plik[1]);
                } catch (FileException e) {
                    System.out.println(e.getMessage());
                }
            }

            else {
                return null;
            }
        }
        return null;
    }

    private String MathOP(String req){
        return null;
    }
}


package web_management;

import java.net.InetAddress;

public class Client {
    int PORT;
    String IP;

    public Client(int localPort, InetAddress inetAddress) {
        this.PORT = localPort;
        this.IP = inetAddress.toString();
    }

    public Client(int localPort, String inetAddress) {
        this.PORT = localPort;
        this.IP = inetAddress;
    }
}

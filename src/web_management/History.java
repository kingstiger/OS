package web_management;

public class History {
    private Client client;
    private String text;
    private boolean isSend; //jezeli wyslano OS->Client =1; jesli client->OS 0

    public History(Client client, String text, boolean isSend){
        this.client = client;
        this.text = text;
        this.isSend = isSend;
    }

    public Client getClient(){
        return this.client;
    }
    public String getText(){
        return this.text;
    }
    public boolean getIsSend(){
        return this.isSend;
    }
}

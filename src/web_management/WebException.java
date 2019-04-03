package web_management;

public class WebException extends Throwable{

    String msg;
    WebException(String msg)
    {
        this.msg = msg;
    }
    @Override
    public String getMessage() { return msg; }
    @Override
    public String toString()
    {
        return msg;
    }

}

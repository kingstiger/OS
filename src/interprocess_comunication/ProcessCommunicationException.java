package interprocess_comunication;

public class ProcessCommunicationException extends Throwable
{
    String msg;
    ProcessCommunicationException(String msg){this.msg=msg;}


    @Override
    public String toString() {return msg;}
}

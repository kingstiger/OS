package process_management;

public class PcbException extends Throwable
{
    String msg;
    PcbException(String msg)
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

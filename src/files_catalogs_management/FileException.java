        package files_catalogs_management;

public class FileException extends Throwable
{
    String msg;
    FileException(String msg)
    {
        this.msg=msg;
    }
    @Override
    public String getMessage() { return msg; }
    @Override
    public String toString()
    {
        return msg;
    }
}
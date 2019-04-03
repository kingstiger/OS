package permissions_management;

public class PermissionException extends Exception
{
    PermissionException(String message) { super(message); }
    @Override
    public String toString()
    {
        return  getMessage();
    }
}

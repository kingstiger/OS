package text_interface;
import java.io.IOException;

public class ShellException extends IOException
{
    public ShellException(String message) { super(message); }

    public ShellException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellException(Throwable cause) {
        super(cause);
    }
}

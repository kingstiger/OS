package assembler_interpreter;

public class InterpreterException extends Throwable
{
    String msg;

    InterpreterException(String msg)
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

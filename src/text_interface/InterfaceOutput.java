package text_interface;

        import java.util.LinkedList;

public interface InterfaceOutput {
    LinkedList<String> ConsoleMessages = new LinkedList<>();

    static void AddConsoleMessage(String message)   //dodawanie wiadomosci do wyswietlania
    {
        ConsoleMessages.add(message + "\n");
    }

    default LinkedList<String> getConsoleMessages()
    {
        return ConsoleMessages;
    }
    default void ClearConsoleMessages()
    {
        ConsoleMessages.clear();
    }
}

package process_management;

public enum ProcessState
{
    Ready, //Proces jest gotowy do przydzielenia mu CPU
    Running, //Proces jest wykonywany
    Waiting, //Proces oczekuje na zasób np. user input
    Terminated; //Proces zakończył działanie
}

package process_management;
import java.util.UUID;
import interprocess_comunication.*;
import java.util.Vector;

public class Pcb
{
    String Id;  //Id ma być stringiem, koniec kropka.
    String Name;
    ProcessState State;
    int ProgramCounter = 0;
    int A, B, FL, S; //Rejestry procesora
    Vector<Msg> msgQueue; //Kolejka komunikatów

    int ileRozkazow = 0;    //Ilość rozkazów dla SRT
    int ProgramSize;    //Rozmiar programu dla SRT

    //PZ
    public String getId(){
        return Id;
    }

    public void setState(ProcessState s)
    {
        State = s;
    }

    Pcb(String ProcessName, int sizeofProgram)
    {
        UUID UniqueId = UUID.randomUUID();  //Tworzenie unikalnego Id procesu
        this.Id = UniqueId.toString();
        this.Name = ProcessName;    //Nazwę procesowi nadaję programista.
        this.State = ProcessState.Ready;    //Na początku każdy proces dostaje status Ready.
        this.ProgramSize = sizeofProgram;
        msgQueue = new Vector<Msg>(); //tworzy nowy wektor
    }

    //Metody dla SRT
    public int getProgramSize()
    {
        return ProgramSize; //zmienna z duzej co to jest? xDDDDD ~517435708
    }
    public void incRozkazy()
    {
        ileRozkazow++;
    }
    public int getRozkazy()
    {
        return ileRozkazow;
    }

    public String getName()
    {
        return Name;
    }

    public void printPcb(int option) //Metoda wykorzystywana w pracy krokowej
    {
        String spaces = ""; //formatowanie step-worka
        int extraspaces = 7 - this.Name.length();
        for(int i = 0; i < extraspaces; ++i)
            spaces = spaces + ' ';

        if(option == 1)
        {
            System.out.format("%s%s\t%s\t%s\t%s\t%s\t%s\t%s\n", this.Name, spaces, this.State.name(), this.A , this.B, this.FL, this.S, this.ProgramCounter);
            //System.out.println(this.Name + '\t' + this.State.name() + '\t' + this.A + '\t' + this.B + '\t' + this.FL + '\t' + this.S + '\t' + this.ProgramCounter);
        }
        else
        {
            System.out.format("%s%s\t%s\n", this.Name, spaces, this.State.name());
            //System.out.println(this.Name + ' ' + this.State.name());
        }

    }
}

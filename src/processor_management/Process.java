package processor_management;
import process_management.*;
//pierwszy element musi być w pewien sposob wyrozniony, dlatego dodaje mu enuma z wartością "important"
/*
Klasa objekt musi składać się z PCB oraz parametrów, które umożliwiają przydzielenie procesora, mowa tu o parametrach:
Tau - przewidywany czas wykonanaia;
tl - pozostały czas wykonania (zależny od Tau, na początku tl = Tau);
T - rzeczywisty czas wykonania, czyli liczba wykonanych rozkazów
rejection - ile razy nasz proces był wywłaszczany, jeśli >5 to przejmie on procesor podczas kolejnego przydzielania
wt - jak długo proces wywłaszczony czeka na ponowne przydzielenie procesora
 */
class Process
{
    private Pcb pcb;
    private ProcessEnum q;
    private int T; //rzeczywisty czas wykonywania
    private int Tau; // przewidywany czas wykonywania
    private int tl; //time left - pozostały czas wykonywania
    private int rejections;//ile razy był wywłaszczany
    private int wt; //czas oczekiwania na przydział procesora
    private int at; // actual time - aktualny czas wykonywania procesu

    public Process(Pcb pcb)
    {
        this.pcb = pcb;
        T = 0;
        Tau = 0;
        rejections = 0;
    }
    //przeciążona metoda equals, po to, zeby moc usunąć object z wektora, w ktorm przechowuje pcb w stanie ready
    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != Process.class)) {
            return false;
        }
        Process other = (Process) o;
        return (other.pcb.getName() == this.pcb.getName());
    }

    /*
        set i get do wszystkich zmiennych zawartych w klasie
     */
    public void incRej()
    {
        rejections++;
    }

    public int getRej() { return rejections; }

    public int get_Tau()
    {
        return Tau;
    }

    public void set_Tau(int Tau)
    {
        this.Tau = Tau;
        this.tl = Tau;
    }


    public void inc_T()
    {
        T++;
    }

    public int get_T()
    {
        return T;
    }

    public void set_T(Pcb pcb, int T)
    {
        if(this.pcb == pcb)
        {
            this.T = T;
        }
    }

    public Pcb getPcb()
    {
        return pcb;
    }

    public void set_que(ProcessEnum r)
    {
        this.q = r;
    }

    public ProcessEnum get_que()
    {
        return q;
    }

    public void decTl()
    {
        if(tl > 1)
        {
            tl--;
        }
    }

    public int get_tl()
    {
        return tl;
    }

    public int get_wt()
    {
        return wt;
    }

    public void inc_wt()
    {
        this.wt++;
    }

    public int get_at() {return at;}

    public void inc_at() {at++;}

    public void set_at() {this.at = 0;}

    public Process()
    {
        System.out.println("Jestem atrapa!");
    }
}

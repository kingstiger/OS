package processor_management;
import process_management.*;


import java.util.Vector;
import java.util.ArrayList;
import java.util.Random;

public class SrtAlgorithm
{

    private static Vector<Process> cont = new Vector<>(20); //tu zbieram procesy w stanie READY
    private static Process DUMMY; //Atrapa - pracuje, kiedy wektor jest pusty
    private static Process RUNNING; //wykonywany proces
    private static final double alpha = 0.5;
    private static ArrayList<Integer> doneTime= new ArrayList<Integer>();
    //private static int TIME = 0; //wszystkie wykonane rozkazy
    private static double T_removed = 0; //czas wyrzuconych procesow
    private static double Process_removed = 0;//wszystkie wykonane procesy -> zakonczone
    private static double ALL_READY = 0; //wszystkie procesy, ktore sie kiedykolwiek pojawily
    private static double WT_removed = 0; //Waitng time juz wyrzuconych procesow
    private static double WT_all = 0; //czas oczekiwania wszystkich procesow

    public static void ini()//tworzenie procesu DUMMY
    {
        String ds = "JP0;"; //polecenie jump 0
        byte[] l = ds.getBytes();//zmiana na tablicę bajtów
        ArrayList<Byte> dummy = new ArrayList<Byte>();//polecenie musi być bajtami

        for(int i=0; i<ds.length(); i++)
        {
            dummy.add(i,l[i]);
        }

        try
        {
            ProcessManager.createProcess(dummy,"DUMMY");
        }catch (PcbException ex)
        {
            ex.printStackTrace();
        }
    }
    public static void Add_rd(Pcb ready) //dodawanie kolejnych procesów do kontenera symulującego prace procesora
    {
        if(cont.size() <= 20)
        {
            ALL_READY++; //liczba wszystkich procesow, ktore sie pojawiły
            Process ready_ = new Process(ready);

            if(ready.getName().equals("DUMMY"))
            {
                DUMMY = ready_;
                RUNNING = DUMMY;

                //MB16012019 ustawienie procesu z kontenera PCB na Running
                RUNNING.getPcb().setState(ProcessState.Running);
                //MB16012019

                ALL_READY--;
                return;
            }
            System.out.println("[SRT] Add_rd: " + ready.getName() + " pid: " + ready.getId());
            if (cont.size() == 0)
            {
                RUNNING.getPcb().setState(ProcessState.Ready);//ustawianie DUMMY na ready
                ready_.set_Tau(5);
                cont.add(ready_);
                RUNNING = cont.elementAt(0);

                //MB16012019 ustawienie procesu z kontenera PCB na Running
                RUNNING.getPcb().setState(ProcessState.Running);
                //MB16012019
            }
            else
            {
                ready_.set_Tau(RANDOM_TAU());
                cont.add(ready_);
                setRunning();
            }
        }
        else
        {
            System.out.println("[SRT] CONTAINER FULL");
        }
    }
    private static int RANDOM_TAU()
    {
        Random rand = new Random();
        return  rand.nextInt(5) + 5;
    }
    public static boolean stepWork(String[] command) //STEPWORK
    {
        if(command[0].equals("SRT"))
        {
            if(command.length == 2)
            {
                if (command[1].equals("READY"))
                {
                    System.out.println("Ready processes: ");
                    printContainer();
                }
                else if (command[1].equals("TAU"))
                {
                    System.out.println("Tau of ready processes: ");
                    printTau();
                }
                else if (command[1].equals("TAT"))
                {
                    System.out.println("Average Turn around time: ");
                    printTAT();
                }
                else if (command[1].equals("WT"))
                {
                    System.out.println("Average Waiting time: ");
                    printWT();
                }
                else
                {
                    System.out.println("Invalid arguments");
                }
            }
            else
            {
                System.out.println("Invalid arguments lenght");
            }
            return true;
        }
        return false;

    }
    public static void setRunning() //ustawianie wykonywanego procesu
    {

        if(cont.isEmpty())//jeśli jest pusty, to naszym running jest DUMMY
        {
            RUNNING = DUMMY;
            System.out.println("[SRT] RUNNING = DUMMY");
        }
        else
        {
            int index = 0; //index procesu, który bedzie wykonywany
            //System.out.println("Search from");
            //printContainer();

            for (int i = 0; i < cont.size(); i++)
            {
                if (cont.elementAt(i).get_que() == ProcessEnum.important) //jeśli wciąż w kolejce jest pierwszy proces
                {
                    RUNNING = cont.elementAt(i);
                    break;
                }
                else //gdy pierwszy proces już wykonano
                {
                    if (cont.elementAt(i).getRej() > 3)
                    {
                        if(RUNNING != DUMMY)
                            RUNNING.incRej();//zwieksza liczbe wywlaszczen danego procesu

                        RUNNING = cont.elementAt(i);
                        break;
                    }
                    if(cont.elementAt(i).get_tl() < cont.elementAt(index).get_tl())
                    {
                        if(RUNNING != DUMMY)
                            RUNNING.incRej();//zwieksza liczbe wywlaszczen danego procesu

                        index = i;
                    }

                }
            }

            //MB16012019 ustawienie procesu z kontenera PCB na Ready
            RUNNING.getPcb().setState(ProcessState.Ready);
            //MB16012019

            //System.out.println("Aktualnie wykonywany proces: " + RUNNING.getPcb().getName());
            RUNNING.incRej();//zwieksza liczbe wywlaszczen procesu
            RUNNING = cont.elementAt(index); //ustawia nowy proces, ktory bedzie wykonywany

            //MB16012019 ustawienie procesu z kontenera PCB na Running
            RUNNING.getPcb().setState(ProcessState.Running);
            //MB16012019


            if(RUNNING != cont.elementAt(0)) //wrzucanie wykonywanego procesu na początek kontenera
            {
                cont.insertElementAt(RUNNING, 0);
                cont.remove(index+1);
            }

            //System.out.println("Wykonywany proces po zmianie: " + RUNNING.getPcb().getName());
            System.out.println("[SRT] new RUNNING = "  + RUNNING.getPcb().getName());
        }
    }
    private static void Remove(Process to_remove) //usuwanie Procesu ktory zostal wykonany
    {
        if(cont.isEmpty())
        {
            statement();
        }
        else
        {
            for (Process p : cont)
            {
                if(p.equals(to_remove))
                {
                    calculate_tau(); //na podstawie czasu rzeczywistego usuwanego procesu jest wyznaczany Tau nastepnego
                    T_removed += to_remove.get_T(); //zwiększa sume czasów wykonywania każdego procesu
                    WT_removed += to_remove.get_wt();   //zwieksza sume czasow oczekiwania kazdego procesu
                    //usuwanie
                    System.out.println("[SRT] Removed element: " + to_remove.getPcb().getName());
                    cont.remove(to_remove);
                    //System.out.println("Container size: " + cont.size());
                    Process_removed++;
                    // printContainer();
                    setRunning();
                    break;
                }
            }
        }
    }
    public static void terminate(Pcb pcb) //usuwanie PCB, ktory zostal wykonany
    {
        for(int i=0; i<cont.size(); i++)
        {
            if(cont.get(i).getPcb().equals(pcb))
            {
                Remove(cont.get(i));
            }
        }
    }
    private static void calculate_tau() //pierwszy proces musi się wykonać po to, żeby dalej algorytm miał parametry do obliczania kolejnych
    {
        if(cont.isEmpty())
        {
            statement();
        }
        else
        {
            for(int i = 0; i < cont.size() ; i++)
            {
                    double value = tau(i - 1); //pobieram i-1
                    cont.elementAt(i).set_Tau((int) value);
            }
        }
    }
    private static int tau(int n) //metoda do obliczania przewidywanego czasu kolejnych procesów
    {
        if(n == -1)
        {
            return 5;
        }

        double value = 0;
        for(int j = 0 ; j<=n ; j++) //j to tak naprawdę waga wykonywanego pocesu
        {
            value += Math.pow((1-alpha),j)*alpha*cont.elementAt(n-j).get_T(); //(1 - α )^j α tn -j + …
            if(j==n)
            {
                value+= Math.pow((1-alpha), n+1) * cont.elementAt(0).get_Tau(); //(1 - α )^(n +1) τ0
            }
        }
        if(value > 3)
            return (int)value;
        else
            return RANDOM_TAU();
    }
    public static void ileRozkazow(Pcb pcb, int ileRozkazow)
    {
        RUNNING.set_T(pcb, ileRozkazow);
    }
    public static Pcb getRunning()//wykonywany proces - get PCB,
    {
        return RUNNING.getPcb();
    }
    public static void timeLeftDecr()
    {
        if(RUNNING.get_tl() > 0) //zmniejszamy TimeLeft
        {
            RUNNING.decTl();
        }
        for(Process p : cont) //zwiekszam WaitingTime pozostałych procesow
        {
            if(p != RUNNING)
            {
                p.inc_wt();
                WT_all++;
            }
        }
    }
    public static Pcb getDummy()
    {
        return DUMMY.getPcb();
    }
    public static void printName()
    {
        for(Process e:cont) {
            System.out.println(e.getPcb().getName());
        }
    }
    public static void printContainer()
    {
        for(Process e:cont)
        {
            System.out.println(
                    "No = " + cont.indexOf(e) +
                            " name = " + e.getPcb().getName() +
                            " T = " + e.get_T() +
                            " Tau = " + e.get_Tau() +
                            " tl = " + e.get_tl() +
                            " wt = " + e.get_wt()
            );
        }
    }
    private static void printTau()
        {
        for(Process e:cont)
        {
            System.out.println(
                    " name = " + e.getPcb().getName() +
                            " Tau = " + e.get_Tau() +
                            " tl = " + e.get_tl());
        }
    }
    public static void printTAT() //sredni czas od READY do TERMINATE, czyli czas wykonanie + czas czekania wykonanych procesów
    {
        if(Process_removed == 0)
            System.out.println("Turn around time: " + 0 + " none process done");
        else
            System.out.println("Turn around time: " + (T_removed + WT_removed) / Process_removed);
    }
    public static void printWT() //czas oczekiwania procesow na przydzielenie procesora
    {
        double avg = 0;
        if(ALL_READY > 0)
            avg = WT_all / ALL_READY;

        System.out.println("Waiting time: " + avg);
    }
    public static void statement()
    {
        System.out.println("Nie mozna wykonac takiej operacji, kontener jest pusty");
    }
}
package synchronization_mechanisms;
import java.util.*;

import process_management.*;
import process_management.ProcessManager.*;
/**Taki ogolny zarys jak to bedzie dzialac i wgl

lock - zamkniecie zamka
unlock - no wiadomo
try lock - zamkniecie zamka bez robienia procesu w stan waiting

Awaitng - kolejka procesow czekajacych na danym zamku na otworzenie go
ID - id procesu ktory zamknal zamek
locked - czy zamek jest otwarty czy zamkniety

 *ogolnie ja nie zajmuje sie uzywaniem tego, pod spodem napisze jak to sie robi.
 *w cond.java w wersji ze zmienna warunkowa, tutaj bez.

*/

public class Mutex {
    private boolean locked;
    private String ID = "";
    private Queue<Pcb> Awaitng = new LinkedList<Pcb>();
    static private Queue<Pcb> AllAwaitng = new LinkedList<Pcb>();

    public void lock(Pcb a){ //ARGUMENT PCB -> pcb procesu ktory wykonuje operacje zabezpieczone zamkiem
        if (!this.locked) {
            this.locked = true;
            ID = a.getId();
            System.out.println("[Mutex] Locked by process " + a.getName());
        } else {
            ProcessManager.ProcessSleep(a);
            this.Awaitng.offer(a);
            AllAwaitng.offer(a);
            System.out.println("[Mutex] Was locked, process " + a.getName() + " begins waiting on lock");
        }
    }

    public void lock() { //DO UZYTKU TYLKO PRZEZ SHELLA!!!
        if(!this.locked) {
            this.locked = true;
            ID = "SHELL";
            System.out.println("[Mutex] Locked by SHELL");
        } else {
            System.out.println("[Mutex] Was locked, no access for SHELL");
        }
    }

    public boolean is_locked(Pcb a) {
        if (locked && a.getId().equals(ID)) return true;
        else return false;
    }
    public boolean is_locked() {
        if (locked && ID.equals("SHELL")) return true;
        else return false;
    }

    public boolean is_locked_for_all() {
        if (locked) return true;
        else return false;
    }

    public void unlock(Pcb a) { //ARGUMENT PCB -> pcb procesu ktory wykonuje operacje zabezpieczone zamkiem
        if(a.getId().equals(ID) && this.locked) {
            this.locked = false;
            System.out.println("[Mutex] Opened by process " + a.getName());
            rmv();
            ID = "";
        } else if (!a.getId().equals(ID) && this.locked) {
            System.out.println("[Mutex] Was locked, no access for process: " + a.getName());
        }
    }


    public void unlock() { //DO UZYTKU TYLKO PRZEZ SHELLA!!!
        if(this.locked && ID.equals("SHELL")) {
            this.locked = false;
            System.out.println("[Mutex] Opened by SHELL");
            rmv();
            ID = "";
        } else if(!ID.equals("SHELL") && locked) {
            System.out.println("[Mutex] Was locked, no access for SHELL");
        }
    }

    private void rmv() {
        for (Pcb pcb:this.Awaitng) {
            System.out.println("[Mutex] Process " + pcb.getName() + " was removed from waiting list");
            ProcessManager.ProcessWakeup(pcb);
            this.Awaitng.remove(pcb);
            if(AllAwaitng.contains(pcb)){
                AllAwaitng.remove(pcb);
            }
        }
    }

    public static boolean stepWork(String[] command)
    {
        if(command[0].equals("MTX")) {
            if(command.length == 2) {
                if (command[1].equals("LIST")) {
                    if (AllAwaitng.size() > 0) {
                        System.out.println("[Mutex] Processes on all the waiting lists:");
                        for (Pcb a : AllAwaitng) {
                            System.out.println(a.getName());
                        }
                    } else System.out.println("[Mutex] Can't view waiting list, it's empty!");
                }
                else {
                    System.out.println("Invalid arguments");
                }
            } else {
                System.out.println("Invalid arguments lenght");
            }
            return true;
        }
        return false;
    }

    public void trylock(Pcb a) {
        if (!locked) {
            locked = true;
            ID = a.getId();
        }
    }
}

/*
ogolnie to uzywa sie tego tak, ze najpierw trzeba ten zamek zadeklarowac. w sensie stworzyc obiekt mutex.
potem tuz przed rozpoczeciem dzialan na zasobach, ktore nie moga byc uzyte przez inny proces w tym czase,
uzywa sie mutex.lock(PCB procesu ktory zamyka) albo mutex.trylock(PCB) -> to drugie nie ustawia procesu na waiting
po zakonczeniu dzialan na tych wrazliwych zasobach
uzywa sie mutex.unlock(PCB procesu ktory chce otworzyc) -> jak to PCB nie bedzie takie samo jak to ktore zamyka,
to nie wyjdzie.
 */
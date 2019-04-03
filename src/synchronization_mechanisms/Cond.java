package synchronization_mechanisms;
import process_management.Pcb;
import process_management.ProcessManager;
import process_management.ProcessState;

import java.util.LinkedList;
import java.util.Queue;

public class Cond {
    private Queue<Pcb> Awaitng = new LinkedList<Pcb>();

    //musialo byc "waiting" zamiast "wait" bo jest metoda "wait" w java.object
    public void waiting(Pcb a, Mutex m) { //ARGUMENT PCB -> pcb procesu ktory wykonuje operacje zabezpieczone zmienna
        Awaitng.offer(a);
        m.unlock(a);
        ProcessManager.ProcessSleep(a);
        System.out.println("[Cond] Process is waiting: " + a.getName() );
    }

    public void signal() {
        if (!Awaitng.isEmpty()) {
            for (Pcb a : Awaitng) {
                ProcessManager.ProcessWakeup(a);
                Awaitng.remove(a);
                System.out.println("[Cond] Process " + a.getName() + " is no longer waiting");
            }
        } else {
            System.out.println("[Cond] Wanted to signal, but nobody's waiting");
        }
    }
}



/*
ogolnie to uzywa sie tego tak, ze najpierw trzeba zadeklarowac zamek i zmienna warunkowa. w sensie stworzyc obiekt mutex i cond.
jesli w trakcie potrzebujemy czekac jeszcze na jakies inne operacje ktore musza sie zakonczyc, to uzywamy zmiennej warunkowej.
przyklad dla brania czegos z jakiegos kontenera

gdzieś w szerszej klasie:

Mutex mutex = new Mutex();
cond kontener_pusty;
cond kontener_pelny;

dla "konsumenta":                                                    | dla "producenta":
                                                                     |
#branie z kontenera                                                  |#dodawanie na kontener
mutex.lock(pcb_konsumenta);                                          |mutex.lock(pcb_producenta);
if(kontener.isEmpty()) kontener_pusty.wait(pcb_konsumenta, mutex);   |if(kontener.isFull()) kontener_pelny.wait(pcb_producenta, mutex);
//branie czegos z kontenera                                          |//dodawanie do kontenera
mutex.unlock(pcb_konsumenta);                                        |mutex.unlock(pcb_producenta);
kontener_pelny.signal();                                             |kontener_pusty.signal();
 */
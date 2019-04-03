//Autorized by Sebastian Lewandowski in 2018
//moduł pcom jest odpowiedzialny za komunikację międzyprocesową za pomocą komumikatów
package interprocess_comunication;
import process_management.Pcb;
import process_management.ProcessManager;
import processor_management.SrtAlgorithm;
import synchronization_mechanisms.Mutex;
import synchronization_mechanisms.Cond;
import ram_memory_management.RAM;
import java.util.ArrayList;


public class ProcessCommunication
{
    public static boolean stepWork(String[] command)
    {
        return false;
    }
    private static Cond check = new Cond();

    public static void send(String consumer_name, String message_text) throws ProcessCommunicationException
    {
        Mutex mutex = new Mutex();


        Pcb consumer = ProcessManager.getPcbByName(consumer_name);
        mutex.lock(SrtAlgorithm.getRunning());
        if(consumer == null)
        {
            throw new ProcessCommunicationException("[Communication] Proces o nazwie "+ consumer_name +" nie istnieje");
        }


        Pcb producer = SrtAlgorithm.getRunning();

        if(message_text.length()>8)
        {
            mutex.unlock(SrtAlgorithm.getRunning());
            throw new ProcessCommunicationException("[Communication] Komunikat jest za duzy");
        }

        Msg com = new Msg(message_text, producer.getName());

        ProcessManager.putMessage(consumer, com);
        System.out.println("[Communication] Wyslano komunikat "+ com.getterM() +" do procesu "+ consumer.getName());
        mutex.unlock(consumer);
        check.signal();
    }

    public static void send(String consumer_name, int message_adr) throws ProcessCommunicationException
    {
        Mutex mutex = new Mutex();
        Pcb consumer = ProcessManager.getPcbByName(consumer_name);
        mutex.lock(SrtAlgorithm.getRunning());
        if(consumer == null)
        {
            throw new ProcessCommunicationException("[Communication] Proces o nazwie "+ consumer_name +" nie istnieje");
        }
        Pcb producer = SrtAlgorithm.getRunning();
        ArrayList<Byte> arr = RAM.readFromMemoryUntilEOF(message_adr);
        arr.remove(arr.size()-1);
        String message_text = Msg.ArrByteTostr(arr);

        System.out.println("[Communication] Odebrano komunikat z ramu "+ message_text);

        if(message_text.length()>8)
        {
            mutex.unlock(SrtAlgorithm.getRunning());
            throw new ProcessCommunicationException("[Communication] Komunikat jest za duzy");
        }

        Msg com = new Msg(message_text, producer.getName());

        ProcessManager.putMessage(consumer, com);
        System.out.println("[Communication] Wyslano komunikat "+ com.getterM() +" do procesu "+ consumer.getName());
        mutex.unlock(SrtAlgorithm.getRunning());
        check.signal();
    }


    public static void send(int consumer_adr, String message_text) throws ProcessCommunicationException
    {
        Mutex mutex = new Mutex();

        ArrayList<Byte> arr = RAM.readFromMemoryUntilEOF(consumer_adr);
        arr.remove(arr.size()-1);
        String consumer_text = Msg.ArrByteTostr(arr);
        Pcb consumer = ProcessManager.getPcbByName(consumer_text);

        mutex.lock(SrtAlgorithm.getRunning());
        if(consumer == null)
        {
            throw new ProcessCommunicationException("[Communication] Proces o nazwie "+ consumer_text +" nie istnieje");
        }

        Pcb producer = SrtAlgorithm.getRunning();

        if(message_text.length()>8)
        {
            mutex.unlock(SrtAlgorithm.getRunning());
            throw new ProcessCommunicationException("[Communication] Komunikat jest za duzy");
        }
        Msg com = new Msg(message_text, producer.getName());
        ProcessManager.putMessage(consumer, com);
        System.out.println("[Communication] Wyslano komunikat "+ com.getterM() +" do procesu "+ consumer.getName());
        mutex.unlock(SrtAlgorithm.getRunning());
        check.signal();
    }

    public static void send(int consumer_adr, int message_adr) throws ProcessCommunicationException
    {
        Mutex mutex = new Mutex();

        ArrayList<Byte> arr = RAM.readFromMemoryUntilEOF(consumer_adr);
        arr.remove(arr.size()-1);
        String consumer_text = Msg.ArrByteTostr(arr);
        Pcb consumer = ProcessManager.getPcbByName(consumer_text);
        mutex.lock(SrtAlgorithm.getRunning());
        if(consumer == null)
        {
            throw new ProcessCommunicationException("[Communication] Proces o nazwie "+ consumer_text +" nie istnieje");
        }

        ArrayList<Byte> arr2 = RAM.readFromMemoryUntilEOF(message_adr);
        arr2.remove(arr2.size()-1);
        String message_text = Msg.ArrByteTostr(arr2);


        Pcb producer = SrtAlgorithm.getRunning();

        if(message_text.length()>8)
        {
            mutex.unlock(producer);
            throw new ProcessCommunicationException("[Communication] Komunikat jest za duzy");
        }
        Msg com = new Msg(message_text, producer.getName());
        ProcessManager.putMessage(consumer, com);
        System.out.println("[Communication] Wyslano komunikat "+ com.getterM() +" do procesu "+ consumer.getName());
        mutex.unlock(producer);
        check.signal();
    }

    public static void receive(String producer, int message_adr)
    {
        Pcb consumer = SrtAlgorithm.getRunning();
        Mutex mutex = new Mutex();
        mutex.lock(consumer);

        Msg message = ProcessManager.getMessage(consumer.getName(), producer);

        if(message.MsgEmpty())
        {
            System.out.println("[Communication] Brak zgodnego komunikatu");
            check.waiting(consumer, mutex);
            return;
        }
        else
        {
            System.out.println("[Communication] Odebrano komunikat "+ message.getterM() +" od procesu "+ message.getterP());
            ArrayList<Byte> data = Msg.strToArrByte(message.getterM());

            try {
                RAM.writeArrToMemoryForMsg(data, message_adr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mutex.unlock(consumer);
            return;
        }
    }

    public static void receive(String producer, String message_text)
    {

        Pcb consumer = SrtAlgorithm.getRunning();

        Mutex mutex = new Mutex();
        mutex.lock(consumer);

        Msg message = ProcessManager.getMessage(consumer.getName(), producer);
        if(message.MsgEmpty())
        {
            System.out.println("[Communication] Brak zgodnego komunikatu");
            check.waiting(consumer, mutex);
            return;
        }
        else
        {
            System.out.println("[Communication] Odebrano komunikat "+ message.getterM() +" od procesu "+ message.getterP());
            message_text = message.getterM();
            mutex.unlock(consumer);
            check.signal();
            return;
        }
    }

    public static void receive(int producer_adr, String message_text)
    {

        Pcb consumer = SrtAlgorithm.getRunning();
        Mutex mutex = new Mutex();
        mutex.lock(consumer);

        ArrayList<Byte> arr = RAM.readFromMemoryUntilEOF(producer_adr);
        arr.remove(arr.size()-1);
        String producer_text = Msg.ArrByteTostr(arr);

        Msg message = ProcessManager.getMessage(consumer.getName(), producer_text);


        if(message.MsgEmpty())
        {
            System.out.println("[Communication] Brak zgodnego komunikatu");
            check.waiting(consumer, mutex);
            return;
        }
        else
        {
            System.out.println("[Communication] Odebrano komunikat "+ message.getterM() +" od procesu "+ message.getterP());
            message_text = message.getterM();
            mutex.unlock(consumer);
            check.signal();
            return;
        }
    }

    public static void receive(int producer_adr, int message_adr) throws ProcessCommunicationException
    {
        Pcb consumer = SrtAlgorithm.getRunning();
        Mutex mutex = new Mutex();
        mutex.lock(consumer);

        ArrayList<Byte> arr = RAM.readFromMemoryUntilEOF(producer_adr);
        arr.remove(arr.size()-1);
        String producer_text = Msg.ArrByteTostr(arr);

        Msg message = ProcessManager.getMessage(consumer.getName(), producer_text);

        if(message.MsgEmpty())
        {
            System.out.println("[Communication] Brak zgodnego komunikatu");
            check.waiting(consumer, mutex);
            return;
        }
        else
        {
            System.out.println("[Communication] Odebrano komunikat "+ message.getterM() +" od procesu "+ message.getterP());
            ArrayList<Byte> data = Msg.strToArrByte(message.getterM());

            try {
                RAM.writeArrToMemoryForMsg(data, message_adr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mutex.unlock(consumer);
            check.signal();
            return;
        }
    }
}
package process_management;
import java.util.Vector;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assembler_interpreter.Interpreter;
import interprocess_comunication.*;
import processor_management.*;
import ram_memory_management.RAM;
import virtual_memory_management.VirtualMemoryManager;
import files_catalogs_management.*;

public class ProcessManager
{
    private static Vector<Pcb> pcbContainer = new Vector<Pcb>();
    //Procesy w stanie READY są wysyłane do błażeja w metodzie CreateProcess

    
    
    public static Pcb getPcbByName(String pname)
    {
        for(Pcb pcb : pcbContainer)
        {
            if(pname.equals(pcb.Name))
                return pcb;
        }

        return null; //Jeżeli nie znajdziemy procesu o danej nazwie zwracamy nowy "pusty" proces
    }

    //Obsługa rejestrów
    public static void setRegister(int value, String registerName) //Ustawia wybrany rejestr procesu RUNNING
    {
        Pcb temp = SrtAlgorithm.getRunning();

        if(registerName.equals("A"))
            temp.A = value;

        if(registerName.equals("B"))
            temp.B = value;

        if(registerName.equals("FL"))
            temp.FL = value;

        if(registerName.equals("S"))
            temp.S = value;

        if(registerName.equals("PC"))
        {
            temp.ProgramCounter = value;
        }

    }

    public static void commandCounterPlusPlus()
    {
        SrtAlgorithm.getRunning().ileRozkazow++;
    }

    public static int getRegister(String registerName) //Pobiera wybrany rejestr procesu RUNNING
    {
        Pcb temp = SrtAlgorithm.getRunning();

        if(registerName.equals("A"))
            return temp.A;

        if(registerName.equals("B"))
            return temp.B;

        if(registerName.equals("FL"))
            return temp.FL;

        if(registerName.equals("S"))
            return temp.S;

        if(registerName.equals("PC"))
            return temp.ProgramCounter;


        return temp.A; //defaultowy jest potrzeby bo #Java xD ~517435708
    }

    public static boolean stepWork(String[] command)
    {
        if(command[0].equals("PCB"))
        {
            if(command.length == 2)
            {
                if (command[1].equals("LIST"))
                {
                    System.out.println("PROCESSES\nNAME\tSTATE");
                    for (Pcb p : pcbContainer)
                    {
                        p.printPcb(0);
                    }

                }
                else if (command[1].equals("LOOKUP"))
                {
                    System.out.println("PROCESSES\nNAME\tSTATE\tA\tB\tFL\tS\tPC");
                    for (Pcb p : pcbContainer)
                    {
                        p.printPcb(1);
                    }
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

    //Tworzenie procesów
    public static void createProcess(String filename, String name) throws PcbException
    {
        //patryk baryła
        ArrayList<Byte> program = null;
        try {
            FilesCatalogsManager.open_file(filename); //Otwieranie pliku z programem dla procesu
            program = FilesCatalogsManager.read_all_file_as_array(filename);    //Przeczytanie programu z pliku
            if(program.size() == 0) throw new PcbException("file is empty");
            FilesCatalogsManager.close_file(filename);
        } catch (FileException e) {
            throw new PcbException(e.toString());
        }
        createProcess(program,name);

    }

    public static void createProcess(ArrayList<Byte> program, String name) throws PcbException
    {
        if(program.size() == 0) throw new PcbException("file is empty");

        int memToAlocate = 0;

        // Alokacja pamieci ~517435708
        String alocator = "";
        for(int i=0; i<program.size(); i++)
        {
            alocator += (char)(byte)program.get(i);
        }


        Pattern pattern = Pattern.compile("\\$ALOCATE\\$(\\d+)\\$"); //tworzenie wzorca dla argumentow i kompilacja programu
        Matcher matcher = pattern.matcher(alocator); //dopasowanie regexa do kodu programu

        if(matcher.find()) //alokacja dodatkowej pamięci
        {
            String bufor = matcher.group(1);
            memToAlocate = Integer.parseInt(bufor);
        }


        alocator = alocator.replaceFirst("\\$ALOCATE\\$(\\d+)\\$",""); //Usuwanie frazy allocate z kodu programu

        program.clear(); //Wyczyszczenie tablicy bajtów z kodu programu

        for(int i=0; i<alocator.length(); i++)
        {
            program.add(i,alocator.getBytes()[i]); //Przepisywanie kodu programu bez alocate
        }
        //Koniec przygotowań alokacji.

        for(int i=0; i<pcbContainer.size(); i++)
        {
            if(pcbContainer.get(i).getName().equals(name))
                throw new PcbException("Process named: "+ name + " already exist.");
        }

        pcbContainer.add(new Pcb(name,program.size()));
        VirtualMemoryManager.load(program,pcbContainer.lastElement(),memToAlocate); //Załadowanie programu do VM
        RAM.populatePcbMap(pcbContainer.lastElement(),memToAlocate); //Dodanie tablicy stronic dla Pcb
        SrtAlgorithm.Add_rd(pcbContainer.lastElement());

        //Praca krokowa - wyświetlanie listy procesów
        System.out.println("[ProcessManager] New process " + name + " has been created");
        stepWork(new String[] {"PCB", "LOOKUP"});
    }

    //Zwiększanie licznika rozkazów
    public static void programCounterPlusPlus()
    {
        Pcb temp = SrtAlgorithm.getRunning();
        temp.ProgramCounter++;


        if(temp.ProgramCounter == temp.ProgramSize)
        {
            terminateProcess(temp);
        }

    }

    //Usuwanie procesów
    public static void terminateProcess(String name) throws PcbException
    {
        if(name.equals("DUMMY"))
        {
            throw new PcbException("Process Dummy Cannot Be Terminated.");
        }

        for(int i=0; i<pcbContainer.size(); i++)
        {
            if(pcbContainer.get(i).getName().equals(name)) {
                terminateProcess(pcbContainer.get(i));
                return;
            }
        }
        throw new PcbException("Process named: "+ name + " does not exist or was already terminated.");
    }


    private static void terminateProcess(Pcb p)
    {
        RAM.releasePcb(p); //Usuwa program z ramu
        VirtualMemoryManager.release(p); //i z VM
        SrtAlgorithm.ileRozkazow(p,p.ileRozkazow); //Powiadomienie SRT ile rozkazów zostało wykonane przez usuwany proces
        SrtAlgorithm.terminate(p);

        //Zamykanie plików otwartych przez proces - moduł plików
        FilesCatalogsManager.close_all_files(p);
        System.out.println("[ProcessManager] Process " + p.getName() + " has been terminated");
        pcbContainer.remove(p);
    }

    //Usuwanie procesów po wylogowaniu - Moduł użytkownicy
    public static void terminateProcessAll()
    {
        while(pcbContainer.size() > 1)
        {
            terminateProcess(pcbContainer.lastElement());
        }
    }

    //Metody dla zamków

    //Metoda usypiająca proces
    public static void ProcessSleep(Pcb p)
    {
        p.setState(ProcessState.Waiting);
        VirtualMemoryManager.setPcLastCommand(p);
        SrtAlgorithm.terminate(p);

        //Przestawianie program-countera na poprzednią instrukcję
    }


    //Metoda budząca proces
    public static void ProcessWakeup(Pcb p)
    {
        p.setState(ProcessState.Ready);
        SrtAlgorithm.Add_rd(p);
    }

    //Funkcje potrzebne przy Komunikacji Miedzyprocesowej
    public static Msg getMessage(String consumer, String producer) //Zwraca pierwsze msg z kolejki procesu o danej nazwie, jeżeli nie ma, zwraca puste msg
    {
        for (Pcb p : pcbContainer)
        {
            if(p.Name == consumer)
            {
                for(int i=0; i<p.msgQueue.size(); i++)
                {
                    Msg com = new Msg(p.msgQueue.get(i).getterM(), p.msgQueue.get(i).getterP());
                    if(com.getterP().equals(producer))
                    {
                        p.msgQueue.removeElement(com);
                        return com;
                    }
                }
            }
        }
        return new Msg();
    }

    public static void putMessage(Pcb consumer, Msg message) //Wrzuca do kolejki komunikatów nowy komunikat
    {
        consumer.msgQueue.addElement(message);
    }
}


package ram_memory_management;

import process_management.Pcb;
import process_management.ProcessManager;
import processor_management.*;
import virtual_memory_management.VirtualMemoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Singleton symulujący pamięć RAM z wykorzystaniem stronicowania.
 *
 * Developing....
 *
 * FRAME - długość ramki w bajtach
 * FRAME_LIMIT - ilość ramek(AVAIABLE_MEMORY/FRAME)
 * AVAILABLE_MEMORY - ilość dostępnej pamięci
 *
 * Dostępne metody:
 *
 * writeArrToMemory - zapisuje podane dane do pamięci, przyjmuje argumenty:
 *
 * toWriteByteArray - Tablica typu Byte.
 *
 * Funkcja wpisuje tablicę przekazaną argumentem do pamięci.
 *
 * Można aktualizować tylko całą stronę, nie można zmienić kawałka, każde wywołanie tej metody wiąże
 * się z podmianą strony na nową.
 *
 * readFromMemory  - Metoda pozwala odczytywać Ramkę o podanym adresie logicznym z pamięci RAM.
 *
 * parametry:
 *  index - Indeks pod którym znajduje się szukana ramka.
 *
 *
 */
public class RAM {

    public static final int FRAME            = 8;
    public static final int FRAME_COUNT      = 16;
    public static final int AVAILABLE_MEMORY = 128;

    private static ArrayList<Byte> memory           = new ArrayList<>();//pamięć
    private static ArrayList<Byte> controlMemory    = new ArrayList<>(FRAME_COUNT); //Zapisuje stan komórki pamięci
                                                                                    // zajęta / wolna
    private static Map<Pcb, PageTable> pcbPageTableMap = new HashMap<>(); // Tablica tablic stron pcb

    /**
     * Konstruktor, przy wywołaniu inicjalizuje wszystkie pola jako 0.
     */

    public static void ini()
    {
        for (int i = 0; i < FRAME_COUNT; i++)
        {
            controlMemory.add((byte)0b00000000);

            for(int j=0; j<FRAME; j++)
            {
                memory.add(j,(byte)0b00000000);
            }
        }
    }

    /*
     * Writing section
     */

    /**
     * @param toWriteByteArray - lista Byte, która zostanie zapisana do pamięci.
     *                         Ramki są wybierane na podstawie algorytmu: First Free.
     *                         Zajmowana jest pierwsza wolna ramka dopóki nie zostanie zapisana cała lista.
     */

    public static void writeArrToMemory(ArrayList<Byte> toWriteByteArray)
    {

        int requestedSize = toWriteByteArray.size();
        int pagesRequested = requestedSize/FRAME;

        if(requestedSize%FRAME != 0)
            pagesRequested++;

        int currentByte = 0;
        int index;

        for (int i = 0; i < pagesRequested; i++)
        {

            index = RAM.findNextFreeFrame();
            VirtualMemoryManager.whichCalled(index);
            for (int j = 0; j < FRAME; j++)
            {
                RAM.memory.set(index*FRAME+j, toWriteByteArray.get(currentByte));
                controlMemory.set(index,(byte)0b10000000);
                currentByte++;
            }
        }
    }

    /**
     * @param toWriteByteArray - lista Byte, która zostanie zapisana do pamięci.
     * @param startAddress - adres logiczny od którego fuinckja rozpoczyna wpisywanie.
     * @return zwraca powodzenie - true, lub niepowodzenie - fałsz.
     */

    public static boolean writeArrToMemoryForMsg(ArrayList<Byte> toWriteByteArray, int startAddress) throws Exception
    {
        int nextWriting = startAddress;

        for (int i = 0; i < toWriteByteArray.size(); i++) {
            if(!(nextWriting > pcbPageTableMap.get(SrtAlgorithm.getRunning()).size() * FRAME)) {
                writeToMemory(toWriteByteArray.get(i), nextWriting);
            } else return false;
            nextWriting++;
        }

        if(!(nextWriting > pcbPageTableMap.get(SrtAlgorithm.getRunning()).size() * 8))
            writeToMemory((byte)0x0, nextWriting);
        else return false;

        return true;
    }

    /**
     * @param toWriteByte - Byte do zapisania do pamięci.
     * @param logicalAddress - adres w który Byte podany w pierwszym parametrze zostanie wpisany.
     */

    public static void writeToMemory(Byte toWriteByte, int logicalAddress)
    {
        int pageNumber = calcPageNumber(logicalAddress);


        PageTable pageTable = pcbPageTableMap.get(SrtAlgorithm.getRunning());

        VirtualMemoryManager.whichCalled(calcPageNumber(logicalAddress));

        int frame = pageTable.getFrame(pageNumber);

        int offset = logicalAddress % FRAME;

        VirtualMemoryManager.whichCalled(logicalAddress/FRAME);
        RAM.memory.set(frame*FRAME + offset, toWriteByte);
    }

    /*
    * Reading section
    */

    /**
     * @param logicalAddress - adres logiczny ktorego zawartosc chcemy przeczytac.
     * @return funckja zwraca przeczytany Byte.
     */

    public static Byte readFromMemory(int logicalAddress){

        int pageNumber = calcPageNumber(logicalAddress);
        PageTable pageTable = pcbPageTableMap.get(SrtAlgorithm.getRunning());

        int frame = pageTable.getFrame(pageNumber);

        VirtualMemoryManager.whichCalled(frame);

        int offset = logicalAddress % FRAME;

        return RAM.memory.get(frame*FRAME + offset);
    }

    //Funkcja zdublowana, pozostawiona dla utrzymania spojnosci
    private static Byte readFromProcessMemory(int logicalAddress)
    {
        PageTable processData = pcbPageTableMap.get(SrtAlgorithm.getRunning());

        int page = logicalAddress/FRAME;

        int character = logicalAddress%FRAME;

        int frame = processData.getFrame(page);

        VirtualMemoryManager.whichCalled(frame);

        return memory.get(frame*FRAME + character);
    }

    /**
     * Funkcja odczytuje z pamięci RAM, dopóki nie przeczyta 0x0.
     * @param startAddress - adres od którego funkcja rozpoczyna czytanie
     * @return funkcja zwraca ArrayList<Byte>, zawierający przeczytane znaki.
     */

    public static ArrayList<Byte> readFromMemoryUntilEOF(int startAddress){

        Byte eof = (byte) 0x0;

        int toGetAddress = startAddress;

        Byte get = readFromProcessMemory(toGetAddress);

        ArrayList<Byte> dummy = new ArrayList<>();

        int limiter = 0;

        while(!get.equals(eof)) {
            if(limiter > 99){
                dummy.clear();
                dummy.add((byte)0b00000000);
            }
            toGetAddress = startAddress++;
            get = readFromProcessMemory(toGetAddress);
            dummy.add(get);
            limiter++;
        }

        return dummy;
    }


    /*
    * Control section
    */

    /**
     * @param pcb - referencja do pcb.
     * @param memToAllocate - dodatkowa rezerwacja pamięci
     */

    public static void populatePcbMap(Pcb pcb, int memToAllocate)
    {
        int tableSize = pcb.getProgramSize() + memToAllocate;
        PageTable table = new PageTable();
        if(pcb.getProgramSize() % FRAME == 0)
            table.createPageTable(tableSize/FRAME);
        else
            table.createPageTable(tableSize/FRAME + 1);

        pcbPageTableMap.put(pcb, table);
    }


    /**
     * Po przejściu procesu w stan terminated funkcja usuwa jego dane z pamięci, w pamięci zostają śmieci,
     * są jednak ignorowane.
     * @param pcb - referencja do obiektu pcb.
     */

    public static void releasePcb(Pcb pcb)
    {
        PageTable table = pcbPageTableMap.get(pcb);

        for(int i=0; i<table.size(); i++)
        {
            int index = table.getFrameToRelease(i);
            if(index != -1)
                controlMemory.set(index, (byte)0x0);
        }
        pcbPageTableMap.remove(pcb);
    }

    /**
     * @param pcb - referencja do obiektu pcb.
     * @param pageNumber - numer strony, która powinna zostać wyczyszczona.
     * @return zwraca indeks ramki którą zwolniono.
     * @throws Exception
     */

    public static int releaseFrame(Pcb pcb, int pageNumber) throws Exception
    {
        int frameNumber = pcbPageTableMap.get(pcb).getFrame(pageNumber);

        Byte fill = (byte)0b00000000;

        if(frameNumber < FRAME_COUNT)
        {
            for (int i = 0; i < FRAME; i++)
            {
                VirtualMemoryManager.changePage(pcb, i, RAM.memory.get(frameNumber*FRAME+i));
                RAM.memory.set(frameNumber * FRAME + i, fill);
            }

            controlMemory.set(frameNumber, (byte)0b00000000);
        }else throw new Exception();

        pcbPageTableMap.get(pcb).setAsInvalid(pageNumber);

        return frameNumber;
    }

    /*
    * Utility section
     */

    /**
     * @param toDecompose - Byte, któryu ma zostać rozłożony na poszczególne bity.
     * @return zwraca tablicę "bitów", wartości true/false.
     */

    private static boolean[] byteDecompose(Byte toDecompose){

        if(toDecompose == null){return null;}

        int mask;

        boolean[] table = new boolean[FRAME];

        mask = AVAILABLE_MEMORY;

        for (int i = 0; i < FRAME; i++) {

            int get = (mask & toDecompose);

            table[i] = get % 2 == 0 && get != 0;

            if(get == 1){
                table[i] = true;
            }

            mask = mask >> 1;

        }

        return table;
    }

    /**
     * @param logicalAddress - adres logiczny procesu.
     * @return indeks strony na której znajduje się ten adres.
     */

    private static int calcPageNumber(int logicalAddress){
        return logicalAddress / FRAME;
    }

    /**
     * @param byteTable - sprawdza czy ramka jest zajęta.
     * @return zwraca true, jeśli jest zajęta.
     */

    private static boolean isOccupied(boolean[] byteTable) {
        return byteTable[0];
    }

    /**
     * Funkcja pomocnicza, znajduje najbliższą wolną ramkę
     * @return zwraca numer wolnej ramki.
     */

    static int findNextFreeFrame()
    {
        for(int i=0; i<FRAME_COUNT; i++)
        {
            Byte controlByte = controlMemory.get(i);

            if(!isOccupied(byteDecompose(controlByte)))
            {
                return i;
            }
        }
        return -1;
    }

    /*
    * Interpreter cooperation section
     */

    /**
     * Funkcja integrująca moduł z interpreterem.
     * Po wywołaniu wysyła kolejny znak znajdujący się w pamięci na podstawie wartości rejestru PC.
     * @return funkcja zwraca następny znak, który jest obsługiwany przez Interpreter.
     */

    public static char getChar()
    {
        char character;

        int index = ProcessManager.getRegister("PC");


        byte temp  = readFromProcessMemory(index);
        VirtualMemoryManager.whichCalled(index/FRAME);

        character = (char)temp;

        return character;
    }

    /*
    * StepWork section
     */

    /**
     * Funkcja deweloperska.
     * Debugging
     */

    public static void printPcbMap(){

        System.out.println("PCB Map: ");

        pcbPageTableMap.forEach((k, v) -> {
            System.out.println("Pcb: " + k.getName());
            PageTable.printPageTable(v);
        });
    }

    /**
     * Funkcja deweloperska.
     * Debugging
     */

    public static void printMemory(){
        System.out.println("Memory: ");

        int currentFrame = 0;
        int frameCount = 0;
        for (Byte e : memory){
            if(currentFrame == 0) System.out.println("----Frame: " + frameCount + "-----");
            if(currentFrame == 7) {
                frameCount++;
                currentFrame = 0;
            }

            System.out.println(e);

            currentFrame++;
        }
    }

    /**
     * Funkcja debugująca dla sekcji stepWork.
     * Wyświetla część pamięci wyszczególnioną w parametrach.
     */

    public static void printMemoryPart(int startIndex, int endIndex){
        System.out.println("Memory: ");

        if(endIndex > 127)
            endIndex = 127;

        if(startIndex < 0 || startIndex > 126)
            startIndex = 0;

        for (int j = startIndex; j <= endIndex; j++) {
            System.out.println("[" + j + "]" + "[" + memory.get(j) + "]" + "[" + (char) ((byte) memory.get(j)) + "]");
        }

    }

    /**
     * Funkcja debugująca dla sekcji stepWork.
     * Wyświetla panel kontrolny pamięci RAM.
     */

    public static void printControlPanel(){
        System.out.println("Contol Memory Panel");
        for (Byte e : controlMemory){
            if(e == -128) System.out.println(1);
            else System.out.println(0);
        }
    }

    /**
     * @param command - komenda wpisywana w shellu.
     * @return zwraca kod zakonczonia: pozytywnie/negatywnie.
     * @throws Exception
     */

    public static boolean stepWork(String[] command) throws Exception{

        if(command[0].matches("RAM")) {
            int x1 = 0;
            int x2 = 7;

            if (command.length == 1) {
                x1 = 0;
                x2 = 7;
            } else if (command.length == 2) {
                try {
                    x1 = Integer.parseInt(command[1]);
                }catch(NumberFormatException e){
                    System.out.println("Thats not a number");
                }
                x2 = x1 + 8;
            } else {
                try {
                    x1 = Integer.parseInt(command[1]);
                    x2 = Integer.parseInt(command[2]);
                }catch(NumberFormatException e){
                    System.out.println("Thats not a number");
                }
            }

            printMemoryPart(x1, x2);
            return true;
        }

        if (command[0].matches("RAMOCC")){

            int end = SrtAlgorithm.getRunning().getProgramSize();

            for (int i = 0; i < end; i++) {
                System.out.println("[" + i + "] " + (char)(byte)readFromMemory(i));
            }
            printControlPanel();

            return true;
        }
        return false;
    }
}

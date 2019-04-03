package ram_memory_management;

import process_management.Pcb;
import virtual_memory_management.VirtualMemoryManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PageTable {

    private ArrayList<Byte> pageTable; // tablica stron, oddzielna dla każdego pcb

    public ArrayList<Byte> getPageTable(){
        return pageTable;
    }

    public PageTable(){
        pageTable = new ArrayList<>();
    }

    /*
    *  Initialization section
    * */

    public void createPageTable(int howLong)
    {
        for (int i = 0; i < howLong; i++) {
            pageTable.add((byte)0b00000000);
        }
    }

    int size()
    {
        return pageTable.size();
    }

    /*
    *  Validation section
    * */

    private boolean isValid(int pageNumber)
    {
        if(pageNumber > pageTable.size()-1){return false;}
        try {
            if (((pageTable.get(pageNumber) & 0b10000000) - 0b10000000) == 0) {
                return true;
            }
            return false;
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Cant check if valid");
            return false;
        }
    }

    public void setAsValid(int page)
    {
        Byte temp = pageTable.get(page);
        pageTable.set(page, (byte)(temp | (byte)0b10000000));
    }

    public void setAsInvalid(int page)
    {
        Byte temp = pageTable.get(page);
        pageTable.set(page, (byte)(temp & (byte)0b00001111));
    }

    /*
    *  Frame management section
    * */

    public void setFrame(int recordNumber, int frame)
    {
        Byte temp = pageTable.get(recordNumber);
        temp =  (byte)(temp & (byte)0b11110000);
        temp = (byte)(temp | frame);
        pageTable.set(recordNumber,temp);
    }

    public int getFrameToRelease(int page)
    {
        if(isValid(page))
        {
            return pageTable.get(page) & (byte)0b00001111;
        }

        return -1;
    }

    public int getFrame(int page)
    {
        int frame;

        if(!isValid(page))
        {
            frame = RAM.findNextFreeFrame();

            if(frame == -1)
            {
                frame = VirtualMemoryManager.pageFault(page,true);
            }else
            {
                VirtualMemoryManager.pageFault(page,false);
            }

            setFrame(page,frame);
            setAsValid(page);
        }else
        {
            frame = pageTable.get(page) & (byte)0b00001111;
        }

        return frame;
    }

    /*
    * StepWork section
    * */

    public static void printPageTable(PageTable pt){
        System.out.println("Wypisywanie tablicy stron");

        for(Byte e : pt.getPageTable()){
            System.out.println(e);
        }
    }

}

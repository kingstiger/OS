package virtual_memory_management;

import com.sun.deploy.util.ArrayUtil;
import process_management.Pcb;
import process_management.ProcessManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ExchangeFile
{

    private ArrayList<Byte> virtualMemory;
    private ArrayList<Integer> indexList;
    private ArrayList<Integer> sizeList;
    private ArrayList<Pcb> pcbList;


    ExchangeFile()
    {
        virtualMemory = new ArrayList<>();
        indexList = new ArrayList<>();
        sizeList = new ArrayList<>();
        pcbList = new ArrayList<>();
    }

    void print()
    {
        for(int i=0; i<virtualMemory.size(); i++)
        {
            System.out.print((char)(byte)virtualMemory.get(i));
            if(virtualMemory.get(i) == 0)
            {
                System.out.print("#");
            }
        }

        System.out.print("\n");

        for(int i=0; i<indexList.size(); i++)
        {
            System.out.println("==============================================================");
            System.out.println("Program: " + pcbList.get(i).getName());
            for(int j=indexList.get(i)+1; j<indexList.get(i)+sizeList.get(i)+1; j++)
            {
                if((j-(indexList.get(i)+1))%8 == 0)
                {
                    if(j != indexList.get(i)+1)
                    {
                        System.out.print("]");
                    }else
                    {
                        System.out.println("\tIndex: " + indexList.get(i));
                        System.out.print("\t\t");
                    }
                    System.out.print("[page "+(j-indexList.get(i)+1)/8 +": ");
                }
                System.out.print((char)(byte)virtualMemory.get(j));

            }
            System.out.println("]\n\t\t\tSize: " + sizeList.get(i) +"\n==============================================================");
        }
    }

    void load(ArrayList<Byte> program, Pcb pcb, int memToAlocate)
    {
        pcbList.add(pcb);

        int padding = 8 - program.size() % 8;
        padding %= 8;

        if(memToAlocate > 0)
        {
            int additionalPadding = 8 - (program.size() + memToAlocate) % 8;
            additionalPadding %= 8;

            padding = memToAlocate + additionalPadding;

        }

        sizeList.add(program.size() + padding);
        indexList.add(virtualMemory.size() - 1);

        virtualMemory.addAll(program);

        for(int i=0; i<padding; i++)
            virtualMemory.add((byte)0b00000000);


    }

    int getPC(Pcb p)
    {

        int which = pcbList.indexOf(p);
        int index = indexList.get(which)+1;
        int size = sizeList.get(which);


        int index_b = index + ProcessManager.getRegister("PC") -1;

        for(int i=index_b; i>index; i--)
        {
            if(virtualMemory.get(i) == 59)
            {
                return i - index + 1;
            }
        }


        return 0;

    }


    void changePage(Pcb pcb, int index, Byte newByte)
    {
        int which = pcbList.indexOf(pcb);
        int first = indexList.get(which) + 1;

        virtualMemory.set(index+first,newByte);
    }

    void release(Pcb pcb)
    {
        int which = pcbList.indexOf(pcb);
        int index = indexList.get(which)+1;
        int size = sizeList.get(which);


        Byte[] dumpedMemory = new Byte[virtualMemory.size()];
        virtualMemory.toArray(dumpedMemory);
        ArrayList<Byte> dump = new ArrayList<>();

        for(int i=0; i<virtualMemory.size(); i++)
        {
            if(i < index || i > index + size-1)
            {
                dump.add(dumpedMemory[i]);
            }
        }


        virtualMemory = dump;


        pcbList.remove(which);
        indexList.remove(which);
        sizeList.remove(which);

        for (int i=which; i<indexList.size(); i++)
        {
            indexList.set(i,indexList.get(i)-size);
        }

    }

    ArrayList<Byte> loadPage(int pageNumber, Pcb pcb)
    {
        int whichProgram = pcbList.indexOf(pcb);
        int index = indexList.get(whichProgram);
        index += 1;

        ArrayList<Byte> buffer = new ArrayList<>();

        for(int i=0; i<8; i++)
        {
            buffer.add(virtualMemory.get(index+i+pageNumber*8));
        }


        return buffer;
    }

    @Override
    public String toString()
    {
        String toReturn = "";
        for(int i=0; i<virtualMemory.size(); i++)
        {
            toReturn = toReturn.concat("=");
        }
        toReturn += "\n";

        for (int i=0; i<virtualMemory.size(); i++)
        {
            toReturn += (char)(byte)virtualMemory.get(i);
        }


        toReturn += "\n";
        for(int i=0; i<virtualMemory.size(); i++)
        {
            toReturn = toReturn.concat("=");
        }

        toReturn += " ||| Size: " + virtualMemory.size();
        toReturn += " ||| pcblistSize: " + pcbList.size();
        toReturn += " ||| sizeSize: " + sizeList.size();
        return toReturn;
    }
}

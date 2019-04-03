package virtual_memory_management;
import process_management.ProcessManager;
import processor_management.*;
import process_management.Pcb;
import ram_memory_management.RAM;

import java.util.ArrayList;
import java.util.Stack;

public class VirtualMemoryManager
{
    static private Stack<VirtualPair> lruStack = new Stack<>();
    static private Stack<VirtualPair> lruBufferStack = new Stack<>();
    static private ExchangeFile exchangeFile = new ExchangeFile();

    private static  VirtualPair find(int page, Pcb running)
    {
        VirtualPair buffer = new VirtualPair();


        while (!lruStack.empty())
        {
            buffer = lruStack.pop();
            if(buffer.pcb == running && buffer.page == page)
            {
                break;
            }
            lruBufferStack.push(buffer);
            buffer = new VirtualPair();
        }

        return buffer;
    }

    private static void pushAll()
    {
        while (!lruBufferStack.empty())
        {
            lruStack.push(lruBufferStack.pop());
        }
    }

    public static void load(ArrayList<Byte> program, Pcb pcb,int memToAlocate)
    {
        exchangeFile.load(program,pcb,memToAlocate);
    }

    public static void whichCalled(int page)
    {

        Pcb running = SrtAlgorithm.getRunning();

        VirtualPair found = find(page, running);
        if(found.page == -1)
        {
            pushAll();
            lruStack.push(new VirtualPair(running,page));
        }else
        {
            pushAll();
            lruStack.push(found);
        }

    }

    public static void changePage(Pcb pcb, int index, Byte toWrite)
    {

        exchangeFile.changePage(pcb,index,toWrite);

    }

    public static void setPcLastCommand(Pcb p)
    {
        ProcessManager.setRegister(exchangeFile.getPC(p),"PC");
    }

    private static VirtualPair bottomStack()
    {
        while (!lruStack.empty())
        {
            lruBufferStack.push(lruStack.pop());
        }

        return lruBufferStack.pop();
    }

    public static int pageFault(int pageNumber, boolean noFreeSpace)
    {
        int releasedFrame = -1;
        if(noFreeSpace)
        {
            try {
                VirtualPair toFree = bottomStack();
                releasedFrame = RAM.releaseFrame(toFree.pcb, toFree.page);
                pushAll();
                }
                catch(java.lang.Exception e)
                {
                    System.out.println("[VirtualMemory] Page fault exception!");
                }

        }

        RAM.writeArrToMemory(exchangeFile.loadPage(pageNumber, SrtAlgorithm.getRunning()));

        return releasedFrame;
    }

    public static void release(Pcb pcb)
    {
        VirtualPair bufor;
        while (!lruStack.empty())
        {
            bufor = lruStack.pop();
            if(bufor.pcb != pcb)
            {
                lruBufferStack.push(bufor);
            }
        }
        pushAll();
        exchangeFile.release(pcb);

    }

    public static boolean stepWork(String[] command)
    {

        if(command[0].equals("LRU"))
        {
            if(command.length == 2)
            {
                if (command[1].equals("STACK"))
                {
                    System.out.println("[LRU STACK]");
                    lruBufferStack = (Stack<VirtualPair>) lruStack.clone();
                    while (!lruBufferStack.empty())
                    {
                        System.out.println(lruBufferStack.pop());
                    }
                    System.out.println("[LRU STACK]");
                }
                else if (command[1].equals("VM"))
                {
                    System.out.println(exchangeFile);

                }
                else if (command[1].equals("ALL"))
                {
                    exchangeFile.print();
                }
                else
                {
                    System.out.println("Invalid arguments");
                }
            }
            else
            {
                System.out.println("Invalid arguments length");
            }

            return true;
        }
        return false;
    }
}

package virtual_memory_management;


import process_management.Pcb;

public class VirtualPair
{
    Pcb pcb;
    Integer page;


    VirtualPair(Pcb pcb, Integer page)
    {
        this.pcb = pcb;
        this.page = page;
    }

    VirtualPair()
    {
        page = -1;
        pcb = null;
    }

    @Override
    public String toString()
    {
        if(pcb == null)
        {
            return ("Process: NULL");
        }
        return ("Process: " + pcb.getName() + " Page: " + page);
    }

}

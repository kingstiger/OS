package text_interface;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.List;

public class ShellUtillites {
    static void ReadStringList(List<String> lst)
    {
        for (String str: lst)
        {
            System.out.println(str);
        }
    }
}

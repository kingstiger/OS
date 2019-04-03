package text_interface;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandList {

    private static ArrayList<String> commands = new ArrayList<String>()
    {
        {
            add("CREATEFILE");  //0
            add("DELETEFILE");  //1     -all
            add("READFILE");    //2     -all
            add("WRITEFILE");   //3     -clear
            add("OPENFILE");    //4     -all
            add("CLOSEFILE");   //5     -all
            add("COPYFILE");    //6     -del
            add("FILELIST");    //7     -attrib
            add("TASKLIST");    //8
            add("RENAME");      //9     -user
            add("USERINFO");    //10    -all
            add("empty");       //11
            add("ADMIN");       //12
            add("ADDUSER");     //13    -admin
            add("DELETEUSER");  //14    -all
            add("LOGIN");       //15
            add("LOGOUT");      //16
            add("CREATEPROCESS");//17
            add("KILL");        //18
            add("WEBON");       //19
            add("WEBLISTEN");   //20
            add("WEBOFF");      //21
            add("WEBSEND");     //22    -file
            add("PING");        //23
            add("NETSTAT");     //24
            add("HELP");        //25
            add("EXIT");        //26
            add("COPYRIGHT");   //27    -all
            add("MOVERIGHT");   //28    -all
            add("DELETERIGHT"); //29    -all
            add("SHARERIGHT");  //30    -all

        }
    };

    private static ArrayList<String> infocommands = new ArrayList<String>()
    {
        {
            add("creates a new empty file \n" +                     //CREATEFILE
                    "[name] creates a new empty file\n" +
                    "[name][data] creates a new file with data ");
            add("deletes a file \n" +                               //DELETEFILE
                    "[name] deletes the file with the given name \n" +
                    "[-all] tries to delete all files ");
            add("reads a file \n" +                                  //READFILE
                    "[name] reads the file with the given name \n" +
                    "[-all] tries to read all files");
            add("writes data to a file \n" +                        //WRITEFILE
                    "[name][data] adds the data at the end of the file \n" +
                    "[name][-clear] clears file and wrtes new data\n" +
                    "[-clear] clears file");
            add("opens a file \n" +                                 //OPENFILE
                    "[name] opens the file with the given name \n" +
                    "[-all] tries to open all files");
            add("closes a file \n" +                                //CLOSEFILE
                    "[name] closes the file with the given name \n" +
                    "[-all] tries to close all files");
            add("copies a file \n" +                                //COPYFILE
                    "[name][name] copies the file with the given name \n" +
                    "[name][name][-del] tries to delete file and then copy");
            add("list of all files \n" +                            //FILELIST
                    "[-attrib] shows files and their attributes");
            add("shows all ready process");                         //TASKLIST
            add("renames a file or a user\n" +                      //RENAME
                    "[name][name] renames the file \n" +
                    "[name][-user] renames current user");
            add("shows information about users \n" +                //USERINFO
                    "shows information about the currently logged in user \n" +
                    "[-all] shows information about all users");
            add("");                                                //komenda usunieta
            add("inverts the admin status to the user \n" +           //ADMIN
                    "[username] inverts the admin status to the user with the given username");
            add("adds a new user \n" +                               //ADDUSER
                    "[username] adds the new user with the given username\n" +
                    "[username][-admin] ads the new user with the admin privileges");
            add("deletes a user \n" +                               //DELETEUSER
                    "[username] deletes the user with the given username \n" +
                    "[username][-all] deletes all users excepts current logged in user");
            add("loggs in a user \n" +                              //LOGIN
                    "[username] loggs in a user with the given username");
            add("logs out the user");                               //LOGOUT
            add("creates a new process \n" +                        //CREATEPROCESS
                    "[filename][processname] creates a new process with the given names");
            add("executes an existing process \n" +                 //KILL
                    "[processname] executes the existing process");
            add("turns on network support");                        //WEBON
            add("starts listen \n" +                                //WEBLISTEN
                    "[port] listens on a given port");
            add("turns off network support");                       //WEBOFF
            add("sends a  message \n" +                             //WEBSEND
                    "[IP][port][data] sends a text message \n" +
                    "[IP][-file][filename] sends a file");
            add("checks the connection \n" +                        //PING
                    "[IP] checks the connection");
            add("displays available clients");                      //NETSTAT
            add("help\n" +                                          //HELP
                    "[commandname] help for a given command");
            add("quits the OS");                                    //EXIT
            add("copies a file right \n" +                          //COPYRIGHT
                    "[filename][username][right] copies given right \n" +
                    "[filename][username][-all] copies all rights");
            add("moves a file right \n" +                           //MOVERIGHT
                    "[filename][username][right] moves given right \n" +
                    "[filename][username][-all] moves all rights");
            add("deletes a file right \n" +                         //DELETERIGHT
                    "[filename][username][right] deletes given right \n" +
                    "[filename][username][-all] deletes all rights");
            add("shares a file rights \n" +                         //SHARERIGHT
                    "[filename][username][right] shares given right \n" +
                    "[filename][username][-all] shares all rights");


/*            add(padRight("WEB") + "view the network status");
            add(padRight("RAM") + "[index][index] shows RAM memory");
            add(padRight("PCB") + "[] shows process PCB");
            add(padRight("FAT") + "[] shows the FAT table");
            add(padRight("LRU") + "displays the LRU stack");
            add(padRight("SRT") + "display the SRT queue");
            add(padRight("RAMOCC") +  "shows RAM properities");*/

        }
    };

    public static String padRight(String s) {
        return String.format("%1$-" + 20 + "s", s);
    }

    static void read_commands()
    {
        int i = 0;
        for (String cmd: infocommands)
        {
            if(!commands.get(i).equals("empty"))
                System.out.println(padRight(commands.get(i)) + cmd.split("\n")[0]);
            i++;
        }
    }

    static int indexOf(String command){
        for(int i = 0; i < commands.size(); i++){
            if(commands.get(i).equals(command)){
                return i;
            }
        }
        return -1;
    }

    public static void read_description(String command) throws ShellException
    {
       int index = indexOf(command.toUpperCase());
       if(index == -1)
           throw new ShellException("Error in help command");
        Arrays.stream(infocommands.get(index).split("\n")).
                forEach(cmd -> System.out.println(padRight(commands.get(index)) +" " + cmd));

    }

}


package assembler_interpreter;


import interprocess_comunication.*;


import processor_management.*;
import ram_memory_management.*;
import process_management.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import files_catalogs_management.*;
import sun.awt.SunHints;
import web_management.WebException;
import web_management.WebManager;

public class Interpreter
{

    private static String stream = "";
    private static int whichCommand = -1;
    private static ArrayList<String> argList = new ArrayList<>();


    private static ArrayList<String> commandTab = new ArrayList<String>()
    {{
        add("AD"); //DODAWANIE 0
        add("SB"); //ODEJMOWANIE 1
        add("ML"); //MNOZENIE 2
        add("DV"); //DZIELENIE 3
        add("IR"); //INKREMENTACJA 4
        add("DR"); //DEKREMENTACJA 5
        add("MV"); //PRZENOSZENIE WARTOSCI 6
        add("CMP"); //PRZYRÓWNANIE WARTOSCI 7 Jesli pierwszy jest wiekszy cmp = 1 jesli mniejszy cmp = -1 jesli rowne cmp = 0


        //OPERACJE NA PLIKACH
        add("CF"); //UTWORZ PLIK 8
        add("DF"); //USUN PLIK 9
        add("OF"); //OTWORZ PLIK 10
        add("CLF"); //ZAMKNIJ PLIK 11
        add("WTF"); //ZAPISZ DO PLIKU 12
        add("RFF"); //ODCZYTAJ Z PLIKU 13

        //PROCESY
        add("CP"); //UTWORZ PROCES 14
        add("KP"); //ZABIJ PROCES 15

        //PROGRAMOWE
        add("JP"); //SKOK BEZWARUNKOWY 16
        add("JZ"); //SKOK JEŚLI FLAGA cmp = 0. 17
        add("JNZ"); //SKOK JEŚLI FLAGA cmp != 0. 18
        add("JG"); //SKOK JEŚLI FLAGA cmp = 1. 19
        add("JS"); //SKOK JEŚLI FLAGA cmp = -1.20

        //IO
        add("OUT"); //WYSWIETL NA EKRAN 21
        add("IN"); //ZCZYTAJ Z KLAWIATURY 22

        //WEB
        add("WO"); //WEB ON 23
        add("WF"); //WEB OFF 24
        add("WS"); //WEB SEND 25
        add("WQ"); //WEB SEND FILE 26
        add("WL"); //WEB LISTEN 27

        //Process Comuniation
        add("STP"); //SEND TO PROCESS 28
        add("RFP"); //RECEIVE FROM PROCESS 29


    }};

    private static void setCommand()
    {
        whichCommand = -1;
        for(int i=0; i<commandTab.size(); i++)
        {
            if(stream.matches(commandTab.get(i) + ".*;"))
            {
                whichCommand = i;

                stream = stream.substring(commandTab.get(i).length(),stream.length()); //usuwam polecenie
            }
        }
    }

    private static void setArgList()
    {
        // mozliwe argsy: [index] number "String"

        Pattern pattern = Pattern.compile("(\\[[\\d\\w]+\\])|(\\-{0,1}\\d+)|(\\\"[\\d\\w\\.]+\\\")|(A|B|(FL)|(PC)|S)"); //tworze wzorzec dla argumentow
        Matcher matcher = pattern.matcher(stream);

        while(matcher.find())
        {
            String bufor = matcher.group();
            if(bufor.matches("\\\".+\\\""))
            {
                bufor = bufor.substring(1,bufor.length()-1);
            }
            argList.add(bufor);
        }

        stream = "";

    }

    public static boolean stepWork(String[] command)
    {
        return false;
    }

    private static void interpret() throws InterpreterException, PcbException, WebException, FileException, ProcessCommunicationException
    {

        setCommand();
        setArgList();

        switch(whichCommand)
        {
            case 0:
            {
                add(argList.get(0), argList.get(1));
            } break;
            case 1:
            {
                sub(argList.get(0), argList.get(1));
            } break;
            case 2:
            {
                mul(argList.get(0), argList.get(1));
            } break;
            case 3:
            {
                div(argList.get(0), argList.get(1));
            } break;
            case 4:
            {
                inc(argList.get(0));
            } break;
            case 5:
            {
                dec(argList.get(0));
            } break;
            case 6:
            {
                mov(argList.get(0),argList.get(1));
            } break;
            case 7:
            {
                compare(argList.get(0),argList.get(1));
            } break;
            case 8:
            {
                if(argList.size() == 1)
                    createFile(argList.get(0));
                else
                    createFile(argList.get(0),argList.get(1));
            }break;
            case 9:
            {
                deleteFile(argList.get(0));
            }break;
            case 10:
            {
                openFile(argList.get(0));
            }break;
            case 11:
            {
                closeFile(argList.get(0));
            }break;
            case 12:
            {
                writeToFile(argList.get(0),argList.get(1));
            }break;
            case 13:
            {
                readFromFile(argList.get(0),argList.get(1),argList.get(2));
            }break;
            case 14:
            {
                createProcess(argList.get(0),argList.get(1));
            }break;
            case 15:
            {
                killProcess(argList.get(0));
            }break;
            case 16:
            {
                jmp(argList.get(0));
            } break;
            case 17:
            {
                jmpConditional(argList.get(0),0);
            } break;
            case 18:
            {
                jmpConditional(argList.get(0),1);
                jmpConditional(argList.get(0),-1);
            } break;
            case 19:
            {
                jmpConditional(argList.get(0),1);
            } break;
            case 20:
            {
                jmpConditional(argList.get(0),-1);
            } break;
            case 21:
            {
                out(argList.get(0));
            } break;
            case 22:
            {
                in(argList.get(0));
            }break;
            case 23:
            {   //WEB ON
                WebManager.NetOn();
            } break;
            case 24:
            {   //WEB OFF
                WebManager.NetOff();
            } break;
            case 25:
            {   //WEBSEND
                webSend(argList.get(0), Integer.parseInt(argList.get(1)), argList.get(2));
            } break;
            case 26:
            {
                //WEBSENDFILE
                webSendFile(argList.get(0), argList.get(1));
            } break;
            case 27:
            { //WEB LISTEN
                WebManager.Listen(Integer.parseInt(argList.get(0)));
            } break;
            case 28:
            {
                send(argList.get(0),argList.get(1));
            } break;
            case 29:
            {
                receive(argList.get(0),argList.get(1));
            } break;

            default:
                throw new InterpreterException("Unrecognized Command.");
        }

        whichCommand = -1;
        argList.clear();

    }


    public static void step() throws PcbException, WebException, FileException //prawdopodobnie bedzie to @Override i wrzucimy do nowego watku.
    {

        stream = "";
        argList.clear();
        whichCommand = -1;

        String test = "";
        char last;
        do
        {

            if(!test.equals(SrtAlgorithm.getRunning().getName()))
            {
                stream = "";
                argList.clear();
                whichCommand = -1;
            }

            test = SrtAlgorithm.getRunning().getName();
            last = RAM.getChar();
            stream += last;

            if (last == ';') //Jest to ostatni znak, pora interpretowac
            {
                System.out.println("[Command] " + stream + " [From Process] " + SrtAlgorithm.getRunning().getName());

                try
                {
                    interpret();
                    SrtAlgorithm.getRunning().incRozkazy();
                    SrtAlgorithm.timeLeftDecr();
                    ProcessManager.commandCounterPlusPlus();
                }catch (FileException ex)
                {
                    if(ex.getMessage().equals("Out of range"))
                    {

                        ProcessManager.setRegister(1,"S");
                        SrtAlgorithm.getRunning().incRozkazy();
                        SrtAlgorithm.timeLeftDecr();
                        ProcessManager.commandCounterPlusPlus();
                        stream = "";
                        argList.clear();
                        whichCommand = -1;
                    }else
                    {
                        throw ex;
                    }
                }catch (InterpreterException exception)
                {
                    System.out.println(exception);

                    whichCommand = -1;
                    argList.clear();
                    stream = "";
                    ProcessManager.terminateProcess(SrtAlgorithm.getRunning().getName());
                    return;
                }catch (PcbException exception)
                {

                    whichCommand = -1;
                    argList.clear();
                    stream = "";

                    System.out.println(exception);
                    SrtAlgorithm.getRunning().incRozkazy();
                    SrtAlgorithm.timeLeftDecr();
                    ProcessManager.commandCounterPlusPlus();


                }catch (ProcessCommunicationException ex)
                {
                    whichCommand = -1;
                    argList.clear();
                    stream = "";

                    System.out.println(ex);
                    ProcessManager.terminateProcess(SrtAlgorithm.getRunning().getName());

                }catch (Exception ex)
                {
                //    ex.printStackTrace();
                    System.out.println("[Assertion fault: Out of scope, process: \"" + SrtAlgorithm.getRunning().getName() + "\" terminated.]");
                    System.out.println(ex);

                    whichCommand = -1;
                    argList.clear();
                    stream = "";
                    ProcessManager.terminateProcess(SrtAlgorithm.getRunning().getName());
                    return;
                }


            }

            if(test.equals(SrtAlgorithm.getRunning().getName()))
            {
                ProcessManager.programCounterPlusPlus();
            }else
            {
                last = ';';
            }

        }while (last != ';');



    }
    private static int getValueFromArgument(String str)
    {
        int x = 0;
        boolean a = str.contains("A") | str.contains("B") | str.contains("FL") | str.contains("PC") | str.contains("S");
        boolean b = str.contains("[");


        if(a && !b)
        {
            x = ProcessManager.getRegister(str);
        }
        else if (b)
        {
            int adress = getValueFromArgument(str.substring(1,str.length()-1));
            x = RAM.readFromMemory(adress);
        }
        else
        {
            x = Integer.parseInt(str);
        }
        return x;
    }

    private static void setValueToArgument(String str, int value) throws InterpreterException
    {
        boolean a = str.contains("A") | str.contains("B") | str.contains("FL") | str.contains("PC") | str.contains("S");
        boolean b = str.contains("[");


        if(a && !b)
        {
            ProcessManager.setRegister(value,str);
        }else if(b)
        {
            int adress = getValueFromArgument(str.substring(1,str.length()-1));


            Byte toWrite = (byte)(value & 0xff);
            RAM.writeToMemory(toWrite, adress);
        }else
        {
            InterpreterException ex = new InterpreterException("Wrong param in arg" + str);
            throw ex;
        }

    }

    private static void add(String arg1, String arg2) throws InterpreterException
    {
        int a = getValueFromArgument(arg1);
        int b = getValueFromArgument(arg2);

        int value = a + b;

        setValueToArgument(arg1,value);

    }

    static void mov(String arg1, String arg2) throws InterpreterException
    {
        setValueToArgument(arg1,getValueFromArgument(arg2));
    }

    static void sub(String arg1, String arg2) throws InterpreterException
    {
        int a = getValueFromArgument(arg1);
        int b = getValueFromArgument(arg2);

        int value = a - b;

        setValueToArgument(arg1,value);

    }

    static void mul(String arg1, String arg2) throws InterpreterException
    {
        int a = getValueFromArgument(arg1);
        int b = getValueFromArgument(arg2);

        int value = a * b;


        setValueToArgument(arg1,value);
    }

    static void div(String arg1, String arg2) throws InterpreterException
    {
        int a = getValueFromArgument(arg1);
        int b = getValueFromArgument(arg2);

        int value = a / b;

        setValueToArgument(arg1,value);
    }

    static void inc(String arg1) throws InterpreterException
    {
        int x = getValueFromArgument(arg1);
        setValueToArgument(arg1,x+1);
    }

    static void dec(String arg1) throws InterpreterException
    {
        int x = getValueFromArgument(arg1);
        setValueToArgument(arg1,x-1);
    }

    static void out(String arg1)
    {
        int x = getValueFromArgument(arg1);

        System.out.println("[Interpreter] Out: " + x);

    }

    static void in(String arg1) throws InterpreterException
    {
        Scanner scanner = new Scanner(System.in);
        int val = scanner.nextInt();
        setValueToArgument(arg1,val);
    }

    static void createFile(String arg1)throws InterpreterException
    {

        try
        {
            FilesCatalogsManager.create(arg1);
        }catch (FileException e)
        {
            throw new InterpreterException(e.toString());
        }
    }

    static void createFile (String arg1, String arg2)throws InterpreterException
    {
        try
        {
            FilesCatalogsManager.create(arg1,arg2);
        }catch (FileException e)
        {
            throw new InterpreterException(e.toString());
        }
    }

    static void deleteFile(String arg1) throws InterpreterException
    {
        try
        {
            FilesCatalogsManager.delete_file(arg1);
        }catch (FileException ex)
        {
            throw new InterpreterException(ex.toString());
        }

    }

    static void jmp(String arg1)
    {
        int x = getValueFromArgument(arg1);

        ProcessManager.setRegister(x-1,"PC");
    }

    static void compare(String arg1, String arg2)
    {
        int x = getValueFromArgument(arg1);
        int y = getValueFromArgument(arg2);

        if(x == y)
            ProcessManager.setRegister(0,"FL");
        else
            ProcessManager.setRegister(Integer.signum(x - y),"FL");

    }

    static void jmpConditional(String arg1, int warunek)
    {
        int x = getValueFromArgument(arg1);

        if(warunek == ProcessManager.getRegister("FL"))
            ProcessManager.setRegister(x-1,"PC");
    }

    static void openFile(String arg1) throws InterpreterException
    {
        try
        {
            FilesCatalogsManager.open_file(arg1,SrtAlgorithm.getRunning());
        }catch (FileException ex)
        {
            throw new InterpreterException(ex.toString());
        }

    }

    static void closeFile(String arg1) throws InterpreterException
    {
        try
        {
            FilesCatalogsManager.close_file(arg1,SrtAlgorithm.getRunning());
        }catch (FileException ex)
        {
            throw new InterpreterException(ex.toString());
        }

    }

    static void writeToFile(String arg1, String arg2)throws InterpreterException
    {
        try {
            FilesCatalogsManager.write_file_string_end(arg1,arg2,SrtAlgorithm.getRunning());
        } catch (FileException e) {
            throw new InterpreterException(e.toString());
        }
    }

    static void readFromFile(String arg1, String arg2, String arg3) throws FileException, InterpreterException
    {
        int whichByte = getValueFromArgument(arg3);

        byte data = 0;


        data = FilesCatalogsManager.read_one_byte_from_file(arg2, whichByte,SrtAlgorithm.getRunning());
        setValueToArgument(arg1, data);
    }

    static void createProcess(String arg1, String arg2) throws PcbException
    {
        ProcessManager.createProcess(arg1,arg2);
    }

    static void killProcess(String arg1) throws PcbException
    {
        ProcessManager.terminateProcess(arg1);
    }

    static void webSend(String ip, int PORT, String textToSend) throws WebException
    {
        WebManager.Send(ip, PORT, textToSend);
    }

    static void webSendFile(String ip, String fileName) throws FileException, WebException
    {
        WebManager.sendFile(ip, fileName);
    }

    static void send(String arg1, String arg2) throws ProcessCommunicationException
    {
        boolean b = arg1.contains("[");
        boolean c = arg2.contains("[");

        int adress1 = 0;
        int adress2 = 0;

        if(b)
        {
            adress1 = getValueFromArgument(arg1.substring(1,arg1.length()-1));
        }

        if(c)
        {
            adress2 = getValueFromArgument(arg2.substring(1,arg2.length()-1));
        }

        if(b && c)
        {
            ProcessCommunication.send(adress1,adress2);
            return;
        }

        if(b)
        {
            ProcessCommunication.send(adress1,arg2);
            return;
        }

        if(c)
        {
            ProcessCommunication.send(arg1,adress2);
            return;
        }

        ProcessCommunication.send(arg1,arg2);

    }

    static void receive(String arg1, String arg2) throws ProcessCommunicationException
    {
        boolean b = arg1.contains("[");
        boolean c = arg2.contains("[");

        int adress1 = 0;
        int adress2 = 0;

        if(b)
        {
            adress1 = getValueFromArgument(arg1.substring(1,arg1.length()-1));
        }

        if(c)
        {
            adress2 = getValueFromArgument(arg2.substring(1,arg2.length()-1));
        }

        if(b && c)
        {
            ProcessCommunication.receive(adress1,adress2);
            return;
        }

        if(b)
        {
            ProcessCommunication.receive(adress1,arg2);
            return;
        }

        if(c)
        {
            ProcessCommunication.receive(arg1,adress2);
            return;
        }

        ProcessCommunication.receive(arg1,arg2);
    }

}
package interprocess_comunication;

import sun.awt.image.PixelConverter;

import java.util.ArrayList;

public class Msg
{
    private String message;
    private String producer;


    public Msg(String msg, String prd)
    {
        message = msg;
        producer = prd;
    }

    public Msg()
    {
        message = null;
        producer = null;
    }

    public String getterM()
    {
        return message;
    }

    public String getterP()
    {
        return producer;
    }

    //Funkcja sprawdzająca czy Msg jest pusty
    public boolean MsgEmpty()
    {
        if(message == null && producer == null)
        {
            return true;
        }
        else return false;
    }


    //Funkcja zamieniająca string na ArrayList
    public static ArrayList<Byte> strToArrByte(String stringToConvert){
        ArrayList<Byte> result = new ArrayList<>();
        for (int i = 0; i < stringToConvert.length(); i++) {
            result.add(((byte) stringToConvert.charAt(i)));
        }


        return result;
    }

    //Funkcja zmieniająca ArrayList na string
    public static String ArrByteTostr(ArrayList<Byte> ArrToConvert){
        StringBuilder x = new StringBuilder();
        for (byte e:ArrToConvert) {
            x.append((char)e);
        }
        return x.toString();
    }
}


//komunikat
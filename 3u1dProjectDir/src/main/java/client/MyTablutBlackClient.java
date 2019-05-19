package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class MyTablutBlackClient {
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
        System.out.println("Inserire tempo disponibile per fare una mossa (in secondi): ");
        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        String tempo=buffer.readLine() + "000";
        String[] array = new String[]{"BLACK", tempo};
        if (args.length>0){
            array = new String[]{"BLACK", tempo, args[0]};
        }
        MyTablutClient.Companion.main(array);
    }
}

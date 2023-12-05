package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ServerSocket Server;
        Socket client;

        try{
            
            Server = new ServerSocket(3000);
             
            HashMap<String, Socket> collegamenti = new HashMap<String, Socket>();
            
            while (true) {
                
                client = Server.accept();
                
                Thread t = new serverMultiThread(client,collegamenti);
                
                t.start();

            }

           
        }
        catch(Exception e){

            System.out.println(e.getMessage());
            System.exit(1);


        }
        

    }
}

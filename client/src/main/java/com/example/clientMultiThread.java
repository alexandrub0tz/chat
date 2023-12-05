package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class clientMultiThread extends Thread{
 
    Scanner tastiera = new Scanner(System.in);
    DataOutputStream out = null;
    ArrayList<String> listaNome;
    boolean vivo = true;

    public clientMultiThread( DataOutputStream out, ArrayList<String> listaNome ){

        this.out = out;
        this.listaNome = listaNome;
        
    }

    public void run(){

        try {


            stampaMenù();
            

            

            while(vivo){
                
                System.out.println("---INSERISCI DESTINATARIO O FUNZIONALITA'");                    
                String cmd = tastiera.nextLine();
                String scelta = cmd;

                if(listaNome.contains(cmd)||cmd.equals("broadcast")){

                    scelta = "s";

                }

                if (listaNome.size() > 1 || cmd.equalsIgnoreCase("esc")) {
                    switch (scelta) {
                        case "esc":
                        case "ESC":

                            chiusuraThread();
                            

                            break;

                        case "s": 

                            inviaMessaggio(cmd);
                            
                            
                            break;


                        case "lista":
                        case "LISTA":
                            
                            stampaLista();
                        
                            break;
                        default:
                            System.out.println("errore");
                            System.out.println("\n");   

                        break;
                
                    }
                }
                else{

                    System.out.println("errore, sei l'unico utente, o nome sbagliato");
                    System.out.println("\n");

                }

            }
            System.out.println("---------------clientMultiThread chiusa-----------------");


           
        } catch (Exception e) {
                
            System.out.println("errore " + e.getMessage());

        }

    }

    private void stampaMenù() throws Exception {

        System.out.println("MENU:");
        System.out.println("per i messaggi brodcast inserisci broadcast come destinatario");
        System.out.println("per i messaggi privati inserisci utente di destinazione");
        System.out.println("digita lista per visualizzare utenti presenti");
        System.out.println("esc per chiudere\n");



    }

    private void chiusuraThread() throws Exception{

        System.out.println("---TERMINAZIONE CONNESSIONE");
        out.writeBytes("#\n");
        vivo = false;

    }

    private void inviaMessaggio(String cmd) throws Exception {

        cmd = cmd + "||";
        System.out.println("---INSERISCI MESSAGGIO\n");
        cmd = cmd + tastiera.nextLine();
        out.writeBytes(cmd +"\n");
        System.out.println("\n");


    }

    private void stampaLista(){

        System.out.println("---LISTA UTENTI");
        System.out.println(listaNome);
        System.out.println("\n");

    }

}
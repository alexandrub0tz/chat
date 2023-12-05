package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.*;

public class serverMultiThread extends Thread{
    
    Socket client;
    

    String input;
    BufferedReader in;
    DataOutputStream out;
    HashMap<String, Socket> collegamenti ;
    String nome ;
    boolean vivo = true;        
    public serverMultiThread(Socket c, HashMap<String,Socket> H){
        try{

            this.client = c;
            collegamenti = H;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());
            
        }catch(IOException e){

            System.out.println(e.getMessage());

        }
        
    }

    public void run(){

        try{

            nameIsDuplicate();
            inizializzazioneUtente();
            aggiungimentoUtente();
            
            while(vivo){
                //viene previsto un messaggio di formattazione (comando/destinatario)||(testo da inviare) in cui testo può essere vuoto
                //viene applicata uno split di || per separare il comando e il testo
                input = in.readLine();
                if(input.equalsIgnoreCase("#")){
                    vivo = false;
                    chiusuraThread();
                    break;
                }
                
                String[] receive = input.split(Pattern.quote("||"));
                String destinatario = receive[0];
                String messaggio = receive[1];
                String scelta = destinatario;

            
                if (collegamenti.containsKey(destinatario)){
                    scelta = "s";
                } 

                switch(scelta){
                    
                    case "s":
                        //messaggi privati
                        invia(destinatario, messaggio);
                        //ACK(destinatario);
                        break;

                    case "broadcast" :
                        //messaggi broadcast
                        invia(messaggio);

                        break;

                    default:
                        break;
                }   

            }
            
            

        }
        catch (Exception e) {
            try{

                // qui gestisce le errori di disconnessioni, se viene chiusa l'utente viene chiusa anche il serverThread collegato
                if(e.getMessage().equals("Connection reset")){
                    System.out.println("chiusura:" + collegamenti.get(nome));
                    collegamenti.remove(nome);
                    out.writeBytes("#\n");
                    client.close();
                    vivo = false;
                    

                }
                else{
                    System.out.println("errore generale " + e.getMessage() + " con " + collegamenti.get(nome));


                }
            }
            catch(Exception ex){

            }

        }
        



    }

    //inviare ai utenti già presenti il nuovo utente

    private synchronized void aggiungimentoUtente() throws Exception {

        
            DataOutputStream outBroadcast = null;
            System.out.println("aggiungimento" + collegamenti.get(nome));

            for(String i : collegamenti.keySet()){
                if(i != nome){
                    outBroadcast = new DataOutputStream(collegamenti.get(i).getOutputStream());
                    outBroadcast.writeBytes("*||" + nome + "\n");
                }
            }
            
        
    }

    //inviare all'utente i nomi dei utenti già presenti

    private void inizializzazioneUtente() throws Exception {
        
            System.out.println("inizzializza" + collegamenti.get(nome));

            String inizializzaNomi = "";

                for (String i : collegamenti.keySet()) {
                    inizializzaNomi += i + ",";
                }

            out.writeBytes("?||"+inizializzaNomi + "\n");
            
       
    }

    //funzione per messaggi privati

    private void invia(String d, String m) throws Exception {
        
            DataOutputStream outPut = new DataOutputStream(collegamenti.get(d).getOutputStream());
            outPut.writeBytes(nome+ "||" + m + "\n");
            
    }

    //funzione per messaggi broadcast

    private void invia(String m) throws Exception {

           for(String i : collegamenti.keySet()){

                DataOutputStream outPut = new DataOutputStream(collegamenti.get(i).getOutputStream());                
                outPut.writeBytes("b||" + m + "\n");

            }
        
    }


    // questa funzione non viene applicata, rimangono dei problemi irrisolti
    private void ACK(String destinatario) {

        try{

            BufferedReader inDestinatario = new BufferedReader(new InputStreamReader(collegamenti.get(destinatario).getInputStream()));

            String ack = inDestinatario.readLine();
            System.out.println("-------------------------" + ack);

            out.writeBytes(ack+"\n");
        }catch(Exception e){
            try{
                out.writeBytes("!ACK");
            }catch(Exception E){

                System.out.println("errore ACK " +E.getMessage() + collegamenti.get(nome));

            }
            

        }

    }

    private void nameIsDuplicate() throws Exception {

            boolean duplicato;
            do{
                nome = in.readLine();
                duplicato = true;
                if(collegamenti.containsKey(nome)){    
                    out.writeBytes("!\n");
                }
                else{
                    
                    collegamenti.put(nome, client);
                    out.writeBytes("ok\n");
                    duplicato = false;
                }

            }while(duplicato);
        
    }

    private void chiusuraThread() throws Exception {
        
            //qui viene gestito la chiusura voluta dal client digitando esc
            for (String i : collegamenti.keySet()) {
                DataOutputStream outBroadcast = new DataOutputStream(collegamenti.get(i).getOutputStream());
                outBroadcast.writeBytes("#||"+nome+"\n");
            }
            System.out.println("chiusura:" + collegamenti.get(nome));
            collegamenti.remove(nome);
            client.close();
            
    }








}
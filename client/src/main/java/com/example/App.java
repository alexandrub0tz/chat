package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "client" );
        Socket s;
       
        DataOutputStream out = null;
        BufferedReader in;
        ArrayList<String> listaNomi = new ArrayList();       
        String receive = "";
        boolean vivo = true;
        String nome;
        try{
            //connessoine server
            s = new Socket("localhost",3000);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new DataOutputStream(s.getOutputStream());

            System.out.println("---server pronto");

            //primo output il proprio nome, che non può duplicare

            nome=inviaNome(out,in);

            

            Thread t = new clientMultiThread(out,listaNomi);
            t.start();

            //booleano vivo gestisce ciclo di vita
            
            while(vivo){

                //viene previsto un messaggio di formattazione (comando/destinatario)||(testo da inviare) in cui testo può essere vuoto
                //viene applicata uno split di || per separare il comando e il testo

                receive = in.readLine();

                String[] messaggi = receive.split(Pattern.quote("||"));
                String nomeUtente = messaggi[0];
                String testo = "";
                if(messaggi.length==2){
                    testo = messaggi[1];
                }
                String cmd = nomeUtente;

                if(listaNomi.contains(nomeUtente)){

                    cmd="s";

                }

                //gestione dei messaggi
                //b per messaggi broadcast
                //s per messaggi privati
                //* notifiche di aggiornamento
                //? per inizializzare la listaNomi, per avere i nomiUtenti giacollegati
                //# per disconnettere
                //ACK !ACK per la giusta connessione

                switch (cmd) {

                    case "b":

                        stampaMessaggio(testo,cmd);
                        //ACK(out);
                        break;

                    case "s":

                        stampaMessaggio(nomeUtente,testo, cmd);
                        
                        break;
                        
                    case "*":

                        notificaAggiungimento(testo, listaNomi);
                        
                        break;

                    case "?":

                        inizializzaLista(testo,listaNomi);
                        
                        break;
                        
                    case "#":
                        
                        vivo = isDisconnessione(nome,testo,listaNomi);
                        

                        break;

                    case "ACK":
                    case "!ACK" :
                            boolean ack = isACK(cmd);
                            if(ack)
                                System.out.println("messaggio a buon fine");
                            else
                                System.out.println("messaggio perso");

                        break;
                    

                    default:
                        System.out.println("problema di ricezione");
                        break;
                }

            }

           
        }catch(Exception e){

            try{
                if(e.getMessage().equals("Connection reset")){
                    System.out.println("-------------errore alla connessione, digita esc per chiudere--------------");
                    vivo = false;
                    
                }
                else{

                    System.out.println("errore " + e.getMessage());
                    out.writeBytes("!ACK");

                }
            }
            catch(Exception ex){

            }

        }
        
    }



    private static void stampaMessaggio(String testo,String type){
    
    
        
        System.out.println("---------------messaggio broadcast--------------------");
        System.out.println(testo);
        System.out.println("----------------------------------------------------");
            
    }
    private static void stampaMessaggio(String nomeUtente,String testo,String type){
    
        System.out.println("-----------------------"+nomeUtente+"--------------------------");
        System.out.println(testo);
        System.out.println("------------------------------------------------------------");
        
    }

    private static void notificaAggiungimento(String testo, ArrayList<String> listaNomi){

            
        System.out.println("--------------" + testo + " si e' riunito");
        listaNomi.add(testo);


    }

    private static String inviaNome (DataOutputStream out , BufferedReader in) throws Exception{
        String receive;
        Scanner tastiera = new Scanner(System.in);
        String nome;
        do{
            System.out.println("---inserisci il tuo nome");
            nome = tastiera.nextLine();
            out.writeBytes(nome+"\n");
            receive = in.readLine();

        }while(receive.equals("!"));

        return nome;

    }

    private static void inizializzaLista(String testo, ArrayList<String> listaNomi){

        if(testo != ""){
            String nomi[] = testo.split(",");

            for(int i =0; i< nomi.length ; i++){

                listaNomi.add(nomi[i]);
                
            }
                            
        }

    }

    
    private static void ACK(DataOutputStream out ) throws Exception{
        out.writeBytes("ACK\n");            

    }


    private static boolean isACK(String cmd) throws Exception{
        
        if(cmd.equals("ACK")){

            return true;

        }

        return false;

    }

    private static boolean isDisconnessione(String nome, String testo, ArrayList<String> listaNomi){
        
        if(nome.equals(testo)){

            System.out.println("disconnessione");
            return false;

        }
        else{
                            
            listaNomi.remove(testo);
            System.out.println("-------disconnessone di " + testo);
            return true;
        }
    }


}
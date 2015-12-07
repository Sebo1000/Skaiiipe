/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.sun.webkit.ThemeClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import services.Message;

/**
 *
 * @author Elie
 */
public class ConnexionServeur extends Thread implements Serializable{
    
    Socket socketServer;
    int id;
    public static int incre = 0;
    private Server serv;
    private InfoServeur info;
    
    
    public ConnexionServeur(java.net.Socket socketServer, Server s) {
        this.socketServer = socketServer;
        id = incre;
        incre++;
        this.serv = s ;
        info = new InfoServeur(socketServer.getInetAddress().toString(), socketServer.getPort(),"Serveur "+id);
    }
    
    public InfoServeur getInfos(){
        return info;
    }
    
    
    @Override 
    public void run(){
        InputStream is = null;
        try {
            is = socketServer.getInputStream();
            ObjectInputStream InputClient = new ObjectInputStream(is);
            OutputStream os = socketServer.getOutputStream();
            ObjectOutputStream outputClient = new ObjectOutputStream(os);
            
            while(!Thread.currentThread().isInterrupted()){
                //for(InfoServeur connex : serv.getListServers())
                    outputClient.writeObject(new Message(Message.LIST_SALONS, serv.getListServers()));  
                while(true){
                    Object msg = InputClient.readObject();
                    System.out.println("ConnexionServeur "+id+": bip ");
                    System.out.println("Message reçu: "+msg);
                    Message m = (Message) msg;
                    switch(m.getType()){
                        case 0:
                            System.out.println("Initialisation");
                            switch( Integer.parseInt(m.getData().toString())){
                                case 1:
                                    System.out.println("L'utilisateur veut rejoindre un salon");
                                    outputClient.writeObject(new Message(Message.LIST_SALONS, "test" ));  
                                break;
                                case 0:
                                    System.out.println("L'utilisateur veut créer un salon");
                                    outputClient.writeObject(new Message(Message.CREATION_SALON, "id"));  
                                break;
                            }
                            break;
                        case 1 :
                            System.out.println("Hello");
                            for(InfoServeur connex : serv.getListServers())
                                outputClient.writeObject(new Message(Message.LIST_SALONS, connex ));
                        break;
                        case 2 :
                            System.out.println("Hello");
                            for(InfoServeur connex : serv.getListServers())
                                outputClient.writeObject(new Message(Message.LIST_SALONS, connex ));
                        break;
                        
                        default:
                            System.out.println("error");
                            outputClient.writeObject(new Message(Message.ERROR, null ));
                  }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnexionServeur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnexionServeur.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(ConnexionServeur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
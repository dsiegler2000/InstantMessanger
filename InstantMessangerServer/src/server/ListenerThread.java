package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//listenens for new users to come online
public class ListenerThread extends Thread{
    
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    public boolean connected = false;
    
    private static int numThreads = 0;
    
    public ListenerThread(){
        
        this.setName("ListenerThread" + numThreads);
        numThreads++;
        
    }
    
    @Override
    public void run(){
        
        connect();
        
    }
    
    private void connect(){
        
        ServerDriver.getServerGraphics().showMessage("Waiting for connections...\n");
        
        try{
            System.out.println("here");
            connection = Server.server.accept();
            System.out.println("Connection established with " + connection.getInetAddress().getHostName());
            ServerDriver.getServerGraphics().showMessage(
                    "Connection established with " 
                    + connection.getInetAddress().getHostName() + "\n");
            setupStreams();
            System.out.println(connected);
            connected = true;
            
        }catch (IOException ioException){ 
            
            ioException.printStackTrace();
            System.out.println("connected = false");
            connected = false;
            
        }
        
    }
    
    private void setupStreams() throws IOException{
        
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        
        input = new ObjectInputStream(connection.getInputStream());
        
        connected = true;
        
    }
    
    public ListenerThread reset(){
        
        return new ListenerThread();
        
    }
    
    public Socket getConnection(){return connection;}
    public ObjectOutputStream getOutput(){return output;}
    public ObjectInputStream getInput(){return input;}
    
}

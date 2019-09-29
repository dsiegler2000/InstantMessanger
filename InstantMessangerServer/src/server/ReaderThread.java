package server;

import java.io.IOException;

public class ReaderThread extends Thread{
    
    private Object received;
    private Object lastReceived;
    private String message = "";
    private String sataliteData = "";
    private int code;        
    
    private final int idx;
    private boolean hasANewMessage;
    
    private static int numThreads = 0;
    
    public ReaderThread(int idx){
        
        this.idx = idx;
        received = new Object();
        hasANewMessage = false;
        
        this.setName("ReaderThread" + numThreads);
        numThreads++;
        
    }
    
    @Override
    public void run(){
                
        while(isAlive()){
            
            listenForMessages();
                        
        }
                
    }
    
    private void listenForMessages(){
        
        lastReceived = received;
        
        try{
        
            lastReceived = received;
            received = Server.getInputs().get(idx).readObject();
        
            if(!lastReceived.equals(received) && received instanceof String){

                message = received.toString();

                if(message.length() >= 7){

                    if(message.substring(0, 7).equals("CODE - ")){

                        code = Integer.parseInt(message.substring(7, 8));
                        sataliteData = message.substring(9);

                    }

                }

            }
            
        }catch(IOException | ClassNotFoundException exception){}
        
    }
    
    public Object getReceived(){return received;}
    public String getMessage(){return message;}
    public String getSataliteData(){return sataliteData;}
    public int getCode(){return code;}
    public int getIdx(){return idx;}
    
}

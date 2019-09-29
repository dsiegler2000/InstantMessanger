package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Arrays;

import userdata.User;

public class ConversationManager extends Thread{
    
    private ArrayList<ObjectInputStream> inputs;
    private ArrayList<ObjectOutputStream> outputs;
    
    private ArrayList<Object> receiveds;
    private ArrayList<String> messages;
     
    private ArrayList<ReaderThread> readerThreads;
    private ArrayList<Object> lastReceiveds;
    
    private ArrayList<User> users;
    private int numOfPeopleInConversation;
    
    private static int numThreads = 0;
    
    private boolean isACode;
    private boolean originalMessage = false;
    private boolean originalImage = false;
    private boolean shouldSendImage = false; 
    
    public ConversationManager(ArrayList<ObjectInputStream> inputs,
            ArrayList<ObjectOutputStream> outputs){
        
        this.inputs = inputs;
        this.outputs = outputs;
        
        users = new ArrayList<>();
        
        isACode = false;
        
        readerThreads = new ArrayList<>();
        lastReceiveds = new ArrayList<>();
                
        for(int i = 0; i < inputs.size(); i++){
            
            lastReceiveds.add(new Object());
            
        }
        
        numOfPeopleInConversation = inputs.size();
        
        this.setName("ConversationManager" + numThreads);
        numThreads++;
        
    }
       
    public ConversationManager(ArrayList<ObjectInputStream> inputs,
            ArrayList<ObjectOutputStream> outputs, ArrayList<User> users){
        
        this.inputs = inputs;
        this.outputs = outputs;
        
        this.users = users;
        
        readerThreads = new ArrayList<>();
        lastReceiveds = new ArrayList<>();
        
        for(int i = 0; i < inputs.size(); i++){
            
            lastReceiveds.add(new Object());
            
        }
        
        numOfPeopleInConversation = inputs.size();
        
        this.setName("ConversationManager" + numThreads);
        numThreads++;
        
    }  
    
    @Override
    public void run(){
        
        ServerDriver.getServerGraphics().showMessage("You are now connected!");
        ServerDriver.getServerGraphics().ableToType(true);
        Server.connected = true;
        
        for(int i = 0; i < inputs.size(); i++){
            
            readerThreads.add(new ReaderThread(i));
            readerThreads.get(i).start();
                        
        }
        
        byte[][] receivedImages = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        {}, {}, {}, {}};
        byte[][] lastImages = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        {}, {}, {}, {}};

        do{
                
            int i;

            for(i = 0; i < numOfPeopleInConversation; i++){
                                
                lastReceiveds.set(i, receiveds.get(i));
                receiveds.set(i, readerThreads.get(i).getReceived());

                if(!lastReceiveds.get(i).equals(receiveds.get(i))){
                    
                    originalMessage = true;

                    if(receiveds.get(i) instanceof String){
                        
                        shouldSendImage = false;

                        messages.set(i, receiveds.get(i).toString());
                        
                        if(messages.get(i).length() >= 7){
                        
                            if(messages.get(i).substring(0, 7).equals("CODE - ")){

                                ServerDriver.getServer().executeCode
                                        (Integer.parseInt(messages.get(i).substring(7, 8)), 
                                        messages.get(i).substring(9), i);
                                isACode = true;

                            }
                        
                        }

                        else{

                            isACode = false;

                        }

                    }

                    else if(receiveds.get(i) instanceof byte[]){
                        
                        shouldSendImage = true;
                        lastImages[i] = receivedImages[i];
                        receivedImages[i] = (byte[]) receiveds.get(i);
                        Arrays.equals(receivedImages[i], lastImages[i]);                           
                       
                        if(receivedImages[i] != null && receivedImages[i] != null && 
                                !Arrays.equals(receivedImages[i], lastImages[i])){
                            
                            originalImage = true;
                            
                        }
                        
                        else{
                            
                            originalImage = false;
                            
                        }

                    }
                    
                }
                
                else{
                    
                    originalMessage = false;
                    
                }
                
                if(!isACode && originalMessage && !shouldSendImage){
                    
                    ServerDriver.getServerGraphics().showMessage(messages.get(i) + "\n");
                    
                    for(int j = 0; j < numOfPeopleInConversation; j++){
                        
                        if(j != i){
                            
                            ServerDriver.getServer().relayMessage(messages.get(i), j);
                            
                        }
                        
                    }
                    
                }
                
                if(!isACode && originalImage && originalMessage && shouldSendImage){
                    
                    for(int j = 0; j < numOfPeopleInConversation; j++){
                        
                        if(j != i){
                            
                            ServerDriver.getServer().relayImage(receivedImages[i], j);
                            
                        }
                        
                    }
                                        
                    if(i == 1){
                        
                        ServerDriver.getServer().relayImage(receivedImages[i], 0);
                        
                    }
                    
                    else{
                        
                        ServerDriver.getServer().relayImage(receivedImages[i], 1);
                        
                    }
                    
                }

            }

        }while(!messages.get(0).equalsIgnoreCase("CLIENT - END") && 
                !messages.get(1).equalsIgnoreCase("CLIENT - END"));
        
    }
    
    public void addToConversation(ObjectInputStream input, ObjectOutputStream output,
            User user){
        
        if(spaceAvalible()){            

            inputs.add(input);
            outputs.add(output);
            users.add(user);
            
            numOfPeopleInConversation++;
            
            readerThreads.add(new ReaderThread(numOfPeopleInConversation));
            
        }
        
    }
    
    public boolean spaceAvalible(){
        
        return inputs.size() > Server.MAX_CHAT_SIZE;
        
    }
    
    public ArrayList<ObjectInputStream> getInputs(){return inputs;}
    public ArrayList<ObjectOutputStream> getOutputs(){return outputs;}
        
    public ArrayList<Object> getReceiveds(){return receiveds;}
    public ArrayList<String> getMessages(){return messages;}
    
    public ArrayList<User> getUsers(){return users;}
    
    public boolean isACode(){return isACode;}
    public boolean isOriginalMessage(){return originalMessage;}
    public boolean isOriginalImage(){return originalImage;}
    public boolean shouldSendImage(){return shouldSendImage;}
    
    public void setInputs(ArrayList<ObjectInputStream> inputs){this.inputs = inputs;}
    public void setOutputs(ArrayList<ObjectOutputStream> outputs){this.outputs = outputs;}
    
    public void setReceiveds(ArrayList<Object> receiveds){this.receiveds = receiveds;}
    public void setMessages(ArrayList<String> message){this.messages = messages;}
    
    public void setUsers(ArrayList<User> users){this.users = users;}

}

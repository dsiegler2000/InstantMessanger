package server;

import userdata.Account;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import userdata.User;

public class Server{
    
    private final static ArrayList<Object> receiveds = new ArrayList<>();
    private final static ArrayList<String> messages = new ArrayList<>();

    private static ArrayList<ObjectOutputStream> outputs = new ArrayList<>();
    private static ArrayList<ObjectInputStream> inputs = new ArrayList<>();
    public static ServerSocket server;
    private static ArrayList<Socket> connections  = new ArrayList<>();
    
    public static boolean connected = false;
    volatile boolean listenerThreadConnected;
    
    private final static int SOCKET_NUMBER = 6789;
    private final static int BUFFER_SIZE = 100;
        
    public static final int MAX_CHAT_SIZE = 16;
    public static final int MAX_PEOPLE = 1024;

    //constructor
    public Server(){
        
        try{
            
            server = new ServerSocket(SOCKET_NUMBER, BUFFER_SIZE);
            
        }catch(IOException ioException){ioException.printStackTrace();}
        
        receiveds.add(new Object());
        receiveds.add(new Object());
        
        messages.add("       ");
        messages.add("       ");
        
    }

    //setup and run the server
    public void startRunning() throws InterruptedException{
        
        ArrayList<ReaderThread> readerThreads = new ArrayList<>();

        ArrayList<String> lastSataliteDatas = new ArrayList<>();
        ArrayList<String> sataliteDatas = new ArrayList<>();
        ArrayList<Integer> codes = new ArrayList<>();
        
        for(int i = 0; i < MAX_PEOPLE; i++){
            
            lastSataliteDatas.add(new String());
            sataliteDatas.add(new String());
            codes.add(new Integer(0));
            
        }
        
        while(true){
            
            listenerThreadConnected = ServerDriver.getListenerThread().connected;

            if(listenerThreadConnected){

                System.out.println("Hi");

                outputs.add(ServerDriver.getListenerThread().getOutput());
                inputs.add(ServerDriver.getListenerThread().getInput());
                
                ServerDriver.getListenerThread().interrupt();
                ServerDriver.setListenerThread(ServerDriver.getListenerThread().reset());
                ServerDriver.getListenerThread().start();
                                
                readerThreads.add(new ReaderThread(inputs.size() - 1));
                //System.out.println(inputs.size() - 1);
                readerThreads.get(inputs.size() - 1).start();
                
                System.out.println("reset()");
                
            }
            
            for(int i = 0; i < readerThreads.size(); i++){
                
                lastSataliteDatas.set(i, sataliteDatas.get(i));
                sataliteDatas.set(i, readerThreads.get(i).getSataliteData());
                
                if(!lastSataliteDatas.get(i).equals(sataliteDatas.get(i))){
                    
                    codes.set(i, readerThreads.get(i).getCode());
                    executeCode(codes.get(i), sataliteDatas.get(i), readerThreads.get(i).getIdx());
                    
                }
                
            }

        }

    }

    /**@deprecated*/
    //wait for connection then tell the user that they are connected
    //and the connection info
    private void waitForConnection() throws IOException, InterruptedException{
        

        ServerDriver.getServerGraphics().showMessage("Waiting for people to connect... \n");
        connections = new ArrayList<>();
        connections.add(server.accept());
        //connections.add(server.accept());
        ServerDriver.getServerGraphics().showMessage("Now connected to " + 
                connections.get(0).getInetAddress().getHostName() + " and " + 
                connections.get(1).getInetAddress().getHostName() + "\n");

        addToOnline();

    }

    /**@deprecated */
    //get stream to send and receive data
    private void setupStreams() throws IOException{

        outputs = new ArrayList<>();
        
        outputs.add(new ObjectOutputStream(connections.get(0).getOutputStream()));
        outputs.get(0).flush();
        //outputs.add(new ObjectOutputStream(connections.get(1).getOutputStream()));
        //outputs.get(1).flush();
        
        inputs.add(new ObjectInputStream(connections.get(0).getInputStream()));
        //inputs.add(new ObjectInputStream(connections.get(1).getInputStream()));

        ServerDriver.getServerGraphics().showMessage("Streams are now setup!\n");

    }

    /**@deprecated */
    //during the chat conversation
    private void whileChatting() throws IOException{
        
        ServerDriver.getServerGraphics().showMessage("You are now connected!");
        ServerDriver.getServerGraphics().ableToType(true);
        connected = true;
        boolean isACode =  false;
        boolean originalMessage = false;
        boolean originalImage = false;
        boolean shouldSendImage = false;
        
        ArrayList<ReaderThread> readerThreads = new ArrayList<>();
        
        readerThreads.add(new ReaderThread(0));
        readerThreads.get(0).start();

        //readerThreads.add(new ReaderThread(1));
        //readerThreads.get(1).start();
        
        ArrayList<Object> lastReceiveds = new ArrayList();
        lastReceiveds.add(new Object());
        //lastReceiveds.add(new Object());

        byte[][] receivedImages = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        {}, {}, {}, {}};
        byte[][] lastImages = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        {}, {}, {}, {}};

        do{
                
            int i;

            for(i = 0; i < receiveds.size(); i++){
                                
                lastReceiveds.set(i, receiveds.get(i));
                receiveds.set(i, readerThreads.get(i).getReceived());

                if(!lastReceiveds.get(i).equals(receiveds.get(i))){
                    
                    originalMessage = true;

                    if(receiveds.get(i) instanceof String){
                        
                        shouldSendImage = false;

                        messages.set(i, receiveds.get(i).toString());
                        
                        if(messages.get(i).length() < 7){
                        
                            if(messages.get(i).substring(0, 7).equals("CODE - ")){

                                executeCode(Integer.parseInt(messages.get(i).substring(7, 8)), 
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
                    
                    if(i == 1){
                        
                        relayMessage(messages.get(i), 0);
                        
                    }
                    
                    else{
                        
                        relayMessage(messages.get(i), 1);
                        
                    }
                    
                }
                
                if(!isACode && originalImage && originalMessage && shouldSendImage){
                                        
                    if(i == 1){
                        
                        relayImage(receivedImages[i], 0);
                        
                    }
                    
                    else{
                        
                        relayImage(receivedImages[i], 1);
                        
                    }
                    
                }

            }

        }while(!messages.get(0).equalsIgnoreCase("CLIENT - END") && 
                !messages.get(1).equalsIgnoreCase("CLIENT - END"));

    }

    //close streams and sockets after you are done chatting
    public static void closeConnection(int idxOfConnection){
        
        ServerDriver.getServerGraphics().showMessage("\nClosing connections...\n");
        ServerDriver.getServerGraphics().ableToType(false);
        connected = false;
        
        try{

            outputs.get(idxOfConnection).close();
            inputs.get(idxOfConnection).close();
            connections.get(idxOfConnection).close();

        }catch(IOException ioException){

            ioException.printStackTrace();

        }
        
        //removeFromOnline();

    }

    //relays a message from clients to client
    public void relayMessage(final String message, int idxOfRecipient){

        try{
            
            if(outputs.get(idxOfRecipient) == null){

                ServerDriver.getServerGraphics().showMessage
                        ("MAKE SURE THAT YOU ARE CONNECTED TO SOMEONE!\n");

            }

            else{

                outputs.get(idxOfRecipient).writeObject(message);
                outputs.get(idxOfRecipient).flush();

            }

        }catch(IOException ioException){

            ServerDriver.getServerGraphics().showMessage("\nERROR! UNABLE TO SEND MESSAGE!");

        }
        
    }
    
    //relays an image from clients to client
    public void relayImage(final byte[] imageToSend, int idxOfRecipient){
        
        try{
            
            if(outputs.get(idxOfRecipient) == null){

                ServerDriver.getServerGraphics().showMessage
                        ("MAKE SURE THAT YOU ARE CONNECTED TO SOMEONE!\n");

            }

            else{
                
                outputs.get(idxOfRecipient).writeObject(imageToSend);
                outputs.get(idxOfRecipient).flush();

            }

        }catch(IOException ioException){

            System.out.println("\nERROR! UNABLE TO SEND IMAGE!");
            ioException.printStackTrace();

        }
        
        ServerDriver.getServerGraphics().showMessage("CLIENT - [JUST SENT AN IMAGE]\n");
        
    }
    
    //sends a message (from the server) to all clients
    public void sendMessage(final String message){
        
        try{
            
            for(int i = 0; i < outputs.size(); i++){
            
                if(outputs.get(i) == null){
                    
                    ServerDriver.getServerGraphics().showMessage
                            ("MAKE SURE THAT YOU ARE CONNECTED TO SOMEONE!\n");
                    
                }
                
                else{
                    
                    outputs.get(i).writeObject("MESSAGE FROM THE SERVER - " +
                            message);
                    outputs.get(i).flush();
                    
                }
                            
            }
            
            ServerDriver.getServerGraphics().showMessage
                    ("MESSAGE FROM THE SERVER - " + message + "\n");
            
        }catch(IOException ioException){
            
            ServerDriver.getServerGraphics().showMessage("\nERROR! UNABLE TO SEND MESSAGE!");
            
        }
        
    }

    /**sends a code to the client
     * 
     * 1 = new user, register them
     * 2 = sending username and password
     * 3 = sending if valid credentials and if it is then sending info
     * @param code the code to send
     * @param sataliteData any satellite data that needs to go along with the message
     */
    public void sendCode(int code, int idxOfRecipient, String sataliteData){

        try{
            if(outputs.get(idxOfRecipient) == null){

                ServerDriver.getServerGraphics().showMessage
                        ("MAKE SURE THAT YOU ARE CONNECTED TO SOMEONE!\n");

            }

            else{

                outputs.get(idxOfRecipient).writeObject("CODE - " + code + " " + sataliteData);
                outputs.get(idxOfRecipient).flush();
                System.out.println("Just sent code " + code);

            }

        }catch(IOException ioException){

            System.out.println("\nERROR! UNABLE TO SEND CODE!");

        }

    }

    /**
    * executes a code
    * 2 = check the new username is valid
    * 3 = receiving credentials to check (for login) and sending if they are valid
    * and if it is then sending info
    * 4 = sending if a new username is valid 
    * 
    */
    public void executeCode(int code, String sataliteData, int fromWho){
        
        System.out.println("Executing code " + code);
        System.out.println("satatliteData " + sataliteData);
        
        switch(code){
            
            case 2:

                ArrayList<String> usernameAndPassword = new ArrayList<>
                        (Arrays.asList(sataliteData.split(" ")));
                System.out.println("username = " + usernameAndPassword.get(0));
                
                if(Account.checkIfValidUser(usernameAndPassword.get(0))){
                    
                    sendCode(4, fromWho, "valid");
                    String password = "";
                    
                    for(int i = 1; i < usernameAndPassword.size(); i++){
                        
                        password += usernameAndPassword.get(i);
                        
                    }
                    
                    User user = new User(usernameAndPassword.get(0), password);
                    user.addUserToUserdata();
                    ServerDriver.getAccounts().setUsersInUserdata(User.getUsersFromUserdata());
                    ServerDriver.getAccounts().addUserToOnline(user);
                    
                }
                
                else{
                    
                    sendCode(4, fromWho, "invalid");
                    
                }
                
                break;

            case 3:
                

                ArrayList<User> usersInUserdata = 
                        ServerDriver.getAccounts().getUsersInUserdata();
                
                String[] usernameAndPasswordToValidate = sataliteData.split(" ");
                String username = usernameAndPasswordToValidate[0];
                String password = "";
                
                for(int i = 1; i < usernameAndPasswordToValidate.length; i++){
                    
                    password += usernameAndPasswordToValidate[i];
                    
                }
                
                System.out.println(usersInUserdata.size());
                
                for(User u : usersInUserdata){
                    
                    if(u.getUsername().equals(username) && 
                            u.getPassword().equals(password)){
                        
                        sendCode(3, fromWho, "valid " + u.getFriends().toString());
                        return;
                        
                    }
                    
                }
                
                sendCode(3, fromWho, "invalid");
                break;

            default:

                System.out.println("ERROR! INVALID CODE!");

        }

    }

    /**NEEDS TO BE IMPLEMENTED*/
    private void addToOnline(){



    }

    /**@deprecated this method is no longer used for the server*/
    //sending an image
    public void sendImage(BufferedImage outgoingImage, int idxOfRecipient){
        
        try{
            if(outputs.get(idxOfRecipient) == null){

                ServerDriver.getServerGraphics().showMessage
                        ("MAKE SURE THAT YOU ARE CONNECTED TO SOMEONE!\n");

            }

            else{
                                
                byte[] imageInBytes;
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ImageIO.write(outgoingImage, "png", byteOutput);
                byteOutput.flush();
                imageInBytes = byteOutput.toByteArray();
                byteOutput.close();
                
                outputs.get(idxOfRecipient).writeObject(imageInBytes);
                outputs.get(idxOfRecipient).flush();

            }

        }catch(IOException ioException){

            System.out.println("\nERROR! UNABLE TO SEND IMAGE!");
            ioException.printStackTrace();

        }
        
        ServerDriver.getServerGraphics().showMessage("SERVER - ");
        ServerDriver.getServerGraphics().showImage(outgoingImage);

    }
    
    /**@deprecated */
    private BufferedImage convertBytesToImage(byte[] bytes){
        
        try{
            
            InputStream inputStream = new ByteArrayInputStream(bytes);
            BufferedImage convertedImage = ImageIO.read(inputStream);
            inputStream.close();
            
            return convertedImage;
            
        }catch(IOException ioException){
            
            System.out.println("Invalid byte array!");
            return null;
            
        }
        
    }
    
    public void start(){
        
        try{
                      
            ListenerThread listenerThread = new ListenerThread();
            startRunning();
        
        }catch(InterruptedException interruptedException){
            
            interruptedException.printStackTrace();
            
        }
        
    }
    
    public static boolean isConnected(){
        
        return connected;
        
    }
    
    public static ArrayList<ObjectInputStream> getInputs(){return inputs;}

}

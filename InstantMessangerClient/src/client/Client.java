package client;

import graphics.LoginField;
import graphics.NewUserRegister;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.Socket;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

public class Client extends JFrame{

    private JTextField userText;
    private ImageTextArea chatWindow;
    private JButton imageButton;
    private JSplitPane splitPane;
    private JPanel leftSideParent;
    private JPanel rightSideParent;
    private JList friendsList;
    
    NewUserRegister register;
    
    private BufferedImage image;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private String serverIP;

    private Object received = new Object();
    private String message = "";
    private int linesPrinted = 0;
    
    private static boolean connected = false;

    private static final String LOCALHOST = "127.0.0.1";
    private static final String PC_IP = "10.0.1.76";
    private static final String LAPTOP_IP_AT_SCHOOL = "10.2.4.125";
    private static final String LAPTOP_IP_AT_HOME = "10.0.1.11";
    static final String HOST = LOCALHOST;

    private final int SOCKET_NUMBER = 6789;
    private final int HEIGHT = 850;
    private final int WIDTH = 400;

    //constructor
    public Client(String host){

        super("Instant Messanger Client");

        serverIP = host;
          
        leftSideParent = new JPanel(new BorderLayout());

        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(

            new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent event){

                    sendMessage(event.getActionCommand());
                    userText.setText("");

                }

            }

        );
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(userText, BorderLayout.CENTER);

        imageButton = new JButton("Attach an Image");
        if(System.getProperty("os.name").equals("Mac OS X")){

            imageButton.setSize(150, 30);

        }

        else{

            imageButton.setSize(150, 20);

        }
        
        imageButton.addActionListener(
        
            new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent event){

                    if(Client.isConnected()){

                        image = getImage();

                        if(image != null){

                            sendImage(image);

                        }   

                    }

                    else{

                        showMessage("You must be connected to someone to send an image!\n");

                    }

                }

            }
          
        );
        topPanel.add(imageButton, BorderLayout.EAST);
        
        leftSideParent.add(topPanel, BorderLayout.NORTH);
                
        chatWindow = new ImageTextArea();
        chatWindow.setEditable(false);
        
        DefaultCaret caret = (DefaultCaret) chatWindow.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        leftSideParent.add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        
        friendsList = new JList(new String[]{"billy", "michael", "wait no not that last one"});

        rightSideParent = new JPanel(new BorderLayout());
        
        rightSideParent.add(friendsList, BorderLayout.CENTER);
        rightSideParent.add(new JLabel("Friends List"), BorderLayout.NORTH);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                leftSideParent, rightSideParent);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(600);
        add(splitPane);
        
        setSize(HEIGHT, WIDTH);
        setVisible(true);

    }

    //connect to the server
    public void startRunning(){

        try{

            connectToServer();
            setupStreams();
            login();  
            whileChatting();

        }catch(EOFException eofException){

            showMessage("\nERROR! SERVER TERMINATED THE CONNECTION!");

        }catch(IOException ioException){

            showMessage("\nERROR! THE SERVER AT " + HOST + " IS NOT YET ONLINE!");


        }finally{

            close();

        }

    }

    //connects to a server
    private void connectToServer() throws IOException{

        showMessage("Attempting connection...\n");

        connection = new Socket(InetAddress.getByName(serverIP), SOCKET_NUMBER);

        showMessage("Connected to server " + connection.getInetAddress().getHostName() + "\n");

    }

    //setup streams to send a receive messages
    private void setupStreams() throws IOException{

        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();

        input = new ObjectInputStream(connection.getInputStream());

        showMessage("Streams are now setup!");
        linesPrinted++;

    }

    //while chatting with the server
    private void whileChatting() throws IOException{

        ableToType(true);
        connected = true;

        do{

            try{

                received = input.readObject();
                
                if(received instanceof String){
                                        
                    message = received.toString();

                    if(message.substring(0, 7).equals("CODE - ")){

                        executeCode(Integer.parseInt(message.substring(7, 8)), message.substring(8));

                    }

                    else{

                        showMessage("\n" + message);

                    }
                
                }
                
                else if(received instanceof byte[]){
                                        
                    byte[] bytes = (byte[]) received;
                    BufferedImage imageToShow = convertBytesToImage(bytes);
                    
                    if(imageToShow != null){
                        
                        showImage(imageToShow);
                        
                    }
                    
                }

            }catch(ClassNotFoundException classNotFoundException){

                showMessage("\nUNKNOW ERROR!");

            }

        }while(!message.equals("SERVER - END"));

    }

    //close the streams and sockets
    private void close(){

        showMessage("\nClosing everything down...\n");
        ableToType(false);
        connected = false;

        try{

            output.close();
            input.close();
            connection.close();

        }catch(IOException ioException){

            ioException.printStackTrace();

        }catch(NullPointerException nullPointerException){
        
            System.out.println("The server is not currently online. This "
                    + "caused a null pointer exception.");
        
        }
        
        if(JOptionPane.showConfirmDialog(this, "Would you like"
                + " to quit out of Instant Messanger?", "WARNING", 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            
            System.exit(0);
            
        }

    }

    //sends a message to the server
    private void sendMessage(String message){

        try{

            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\nCLIENT - " + message);

        }catch(IOException ioException){

            chatWindow.append("\nUNKNOWN ERROR!");

        }

    }

    //updates the chatWindow and displays a message
    private void showMessage(final String message){
        
        SwingUtilities.invokeLater(

            new Runnable(){

                @Override
                public void run(){

                    chatWindow.append(message);

                }

            }

        );
        
        chatWindow.setCaretPosition(chatWindow.getDocument().getLength());
        linesPrinted++;

    }

    //gives user permission to type
    private void ableToType(final boolean tof){

        SwingUtilities.invokeLater(

            new Runnable(){

                @Override
                public void run(){

                    userText.setEditable(tof);

                }

            }

        );

    }

    /**executes a given code
     * 
     * 1 = setup new user (depreciated)
     * 2 = sending new username and password
     * 3 = sending credentials to check / receiving if they are valid and if they
     * are then receiving info
     * 4 = receiving if new username is available
     */
    private void executeCode(int code, String sataliteData){

        System.out.println("Receiving " + code);
        System.out.println("sataliteData " + sataliteData);

        switch(code){
            
            case 3:
                
                if(sataliteData.contains(" valid")){
                    
                    JOptionPane.showMessageDialog(this, "Successfully logged in!");
                    //friends = sataliteData.substring(6);
                    
                }
                
                else{
                    
                    login0("Incorrect username or password!");
                    
                }
                
                break;
                
            case 4:
                
                if(sataliteData.equals(" valid")){
                    
                    register.jFrame.dispose();
                    JOptionPane.showMessageDialog(this, "Congratulations!\n"
                            + "You are sucessfully registed and logged in!");
                    
                }
                
                else{
                    
                    setupNewUser("That username is already taken!");
                    
                }
                
                break;

            default:

                System.out.println("ERROR! INVALID CODE!");
                
        }

    }

    private void setupNewUser(String message){
        
        register = new NewUserRegister();
        register.setAlertLabelText(message);
        
        register.waitUntilReady();
        
        if(register.passwordsMatch() && register.isValid()){
            
            sendCode(2, register.getUsername() + " " + register.getPassword());
            register.jFrame.dispose();
            
        }
        
        else if(!register.passwordsMatch()){
            
            register.jFrame.dispose();
            setupNewUser("Your passwords don't match!");
            
        }
        
        else{
            
            register.jFrame.dispose();
            setupNewUser("Your username can't have spaces!");
            
        }

    }

    //sends a code to the client
    //1 = setup new user
    //2 = sending new username and password for checking
    //3 = sending credentials to check / receiving if they are valid and if they
    //are then receiving info
    //4 = receiving if new username is avalible
    public void sendCode(int code, String sataliteData){

        try{
            
            if(output == null){

                showMessage("MAKE SURE THAT YOU ARE CONNECTED TO SOMEONE!\n");

            }

            else{

                output.writeObject("CODE - " + code + " " + sataliteData);
                output.flush();
                System.out.println("Just sent code " + code);

            }

        }catch(IOException ioException){

            System.out.println("\nERROR! UNABLE TO SEND CODE!");

        }

    }

    /**NEEDS TO BE IMPLEMENTED FULLY*/
    private void login(){

        String option = (String) JOptionPane.showInputDialog(this,
                "Welcome to Instant Messanger! Please select Login "
                + "to login, select Login as Guest to proceed as a guest, "
                + " or click Register Account to register a new account", "Login",
                    JOptionPane.QUESTION_MESSAGE, null, new String[]
                    {"Login", "Register New Account"}, "Default");

        if(option == null){

            setupNewUser("");

        }

        else if(option.equals("Register New Account")){

            setupNewUser("");

        }

        else if(option.equals("Login")){
            
            login0("");
            
        }

    }
    
    private void login0(String whatToDisplay){
                
        LoginField loginField = new LoginField();
        
        if(!whatToDisplay.equals(null) || !whatToDisplay.equals("")){
            
            loginField.setMessageLabel(whatToDisplay);
            
        }
        
        loginField.waitUntilReady();
        
        if(!loginField.getUsername().equals(null) && 
                !loginField.getPassword().equals(null) &&
                !loginField.getUsername().equals("") &&
                !loginField.getPassword().equals("")){

            sendCode(3, loginField.getUsername() + " " + loginField.getPassword());

        }

        else{

            login0("You have to input something for your username and password!");

        }
        
    }
    
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
    
    private void showImage(final BufferedImage image){
        
        showMessage("\nCLIENT -");
        
        Font font = chatWindow.getFont();
        Canvas canvas = new Canvas();
        FontMetrics fontMetrics = canvas.getFontMetrics(font);
        
        double scaleFactor = 200d / (double) image.getWidth();
        
        chatWindow.addImage(image, 3, linesPrinted * (fontMetrics.getHeight()), 
                (int) (image.getWidth() * scaleFactor), 
                (int) (image.getHeight() * scaleFactor));
        
        for(int i = 0; i < (image.getHeight() * scaleFactor / 
                this.getFont().getSize()); i++){
            
            showMessage("\n");
            
        }
        
    }
    
    //gets an image from the user and returns it
    private BufferedImage getImage(){
        
        FileDialog fileDialog = new FileDialog(this, 
                "Choose an Image to Send", FileDialog.LOAD);
        
        fileDialog.setFilenameFilter(
        
            new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name){

                    return name.endsWith(".jpeg") || name.endsWith(".gif")
                        || name.endsWith(".png") || name.endsWith(".bmp")
                        || name.endsWith(".jpg");

                }

            }
        
        );
        
        fileDialog.setVisible(true);
        
        if(fileDialog.getFile() != null && 
                (fileDialog.getFile().endsWith(".jpeg") || 
                fileDialog.getFile().endsWith(".gif") || 
                fileDialog.getFile().endsWith(".png") || 
                fileDialog.getFile().endsWith("bmp") || 
                fileDialog.getFile().endsWith("jpg"))){
            
            String filePath = fileDialog.getDirectory() + fileDialog.getFile();
            
            BufferedImage returnImage;
            
            try{
                
                File imageFile = new File(filePath);
                returnImage = ImageIO.read(imageFile);
                
            }catch(IOException ioException){
                
                ioException.printStackTrace();
                showMessage("ERROR! INVALID FILE!");
                return null;
                
            }
            
            return returnImage;
            
        }
        
        else{
            
            showMessage("\nPlease select an image.");
            return null;
            
        }
        
    }
    
    //sending an image
    private void sendImage(BufferedImage outgoingImage){
        
        try{
            
            if(output == null){
                
                showMessage("MAKE SURE THAT YOU ARE CONNECTED TO SOMEONE!\n");
                
            }
            
            else{
                
                byte[] imageInBytes;
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ImageIO.write(outgoingImage, "png", byteOutput);
                byteOutput.flush();
                imageInBytes = byteOutput.toByteArray();
                byteOutput.close();
                
                output.writeObject(imageInBytes);
                output.flush();
                
            }
            
        }catch(IOException ioException){
            
            System.out.println("\nERROR! UNABLE TO SEND IMAGE!");
            ioException.printStackTrace();
            
        }
        
        showImage(outgoingImage);
        
    }
    
    public static boolean isConnected(){
        
        return connected;
        
    }

}

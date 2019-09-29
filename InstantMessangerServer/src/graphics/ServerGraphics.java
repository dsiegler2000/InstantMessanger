package graphics;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import server.ImageTextArea;
import server.Server;
import server.ServerDriver;

public class ServerGraphics extends JFrame{
    
    private JTextField userText;
    private final ImageTextArea chatWindow;
    private final JButton imageButton;
    private final JSplitPane splitPane;
    private JPanel leftSideParent;
    private JPanel topPanel;
    
    private BufferedImage image;
    
    private int linesPrinted = 0;
    
    private final int HEIGHT = 850;
    private final int WIDTH = 400;
    
    //constructor
    public ServerGraphics(){
       
        super("Instant Messenger Server");
        
        leftSideParent = new JPanel(new BorderLayout());
        topPanel = new JPanel(new BorderLayout());
        
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
        
            new ActionListener(){
                
                @Override
                public void actionPerformed(ActionEvent event){
                    
                    ServerDriver.getServer().sendMessage(event.getActionCommand());
                    userText.setText("");
                     
                }
                
            }
        
        );
        
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
                    
                    if(Server.isConnected()){
                        
                        image = getImage();
                        
                        if(image != null){
                            
                            ServerDriver.getServer().sendImage(image, 0);
                            ServerDriver.getServer().sendImage(image, 1);
                            
                        }
                        
                    }
                    
                    else{
                        
                        showMessage("You must be connected to someone to "
                                + "send an image!\n"); 
                        
                    }
                    
                }
                
            }
        
        );
        
        leftSideParent.add(topPanel, BorderLayout.NORTH);
        
        chatWindow = new ImageTextArea();
        chatWindow.setEditable(false);
        
        DefaultCaret caret = (DefaultCaret) chatWindow.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        leftSideParent.add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftSideParent, new JPanel());
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(600);
        add(splitPane);
        
        setSize(HEIGHT, WIDTH);
        setVisible(true);
        
    } 
    
    //updates chatWindow and displays a message
    public void showMessage(final String text){
        
        final Font defaultSystemFont = chatWindow.getFont();
        int defaultFontSize = chatWindow.getFont().getSize();
        final Font bold = new Font(defaultSystemFont.getFontName(), Font.BOLD, defaultFontSize);
        
        SwingUtilities.invokeLater(
        
            new Runnable(){
                
                @Override
                public void run(){
                    
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    
                    chatWindow.setFont(bold);
                    chatWindow.append("[" + sdf.format(cal.getTime()) + "]");
                    chatWindow.setFont(defaultSystemFont);
                    chatWindow.append(text);
                    
                }
                
            }
        
        );
        
        linesPrinted++;
        chatWindow.setCaretPosition(chatWindow.getDocument().getLength());
        
    }
    
    /**@deprecated 
     displays an image to the user*/
    public void showImage(BufferedImage imageToShow){
                        
        Font font = chatWindow.getFont();
        Canvas canvas = new Canvas();
        FontMetrics fontMetrics = canvas.getFontMetrics(font);
        
        double scaleFactor = 200d / (double) imageToShow.getWidth();
        
        chatWindow.addImage(imageToShow, 3, linesPrinted * fontMetrics.getHeight(),
                (int) (image.getWidth() * scaleFactor), 
                (int) (image.getHeight() * scaleFactor));
        
        for(int i = 0; i < (image.getHeight() * scaleFactor / 
                this.getFont().getSize()) - 2; i++){
            
            showMessage("\n");
            
        }
        
    }
    
    //lets the user type a message into userText
    public void ableToType(boolean tof){
        
        userText.setEditable(tof);
        
    }
    
    /**@deprecated 
     * gets and returns a BufferedImage from the user
     * @return a BufferedImage from the user
     */
    public BufferedImage getImage(){
        
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
                ServerDriver.getServerGraphics().showMessage("ERROR! INVALID FILE!");
                return null;
                
            }
            
            return returnImage;

        }
        
        else{
            
            ServerDriver.getServerGraphics().showMessage("\nPlease select an image.");
            return null;
            
        }
        
    }
    
}

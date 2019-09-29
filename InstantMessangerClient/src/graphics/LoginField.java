package graphics;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginField {
        
    private JFrame jFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel label;
    private JLabel messageLabel;
    private JButton submitButton;
    
    private boolean ready = false;
    
    public LoginField(){
        
        jFrame = new JFrame("Register New User");
        jFrame.setSize(400, 155);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        messageLabel = new JLabel("sdf");
        messageLabel.setLocation(105, 2);
        messageLabel.setSize(300, 15);
        messageLabel.setForeground(Color.red);
        jFrame.add(messageLabel);
        
        label = new JLabel("Username: ");
        label.setLocation(10, 20);
        label.setSize(label.getPreferredSize());
        jFrame.add(label);
        
        usernameField = new JTextField();
        usernameField.setColumns(15);
        usernameField.setLocation(150, 20);
        usernameField.setSize(usernameField.getPreferredSize());
        usernameField.setToolTipText("Enter Username");
        jFrame.add(usernameField);
        
        label = new JLabel("Password: ");
        label.setLocation(10, 50);
        label.setSize(label.getPreferredSize());
        jFrame.add(label);
        
        passwordField = new JPasswordField();
        passwordField.setColumns(15);
        passwordField.setSize(passwordField.getPreferredSize());
        passwordField.setLocation(150, 50);
        passwordField.setToolTipText("Enter Password");
        jFrame.add(passwordField);
        
        submitButton = new JButton("Submit");
        submitButton.setLocation(185, 85);
        submitButton.setSize(100, 20);
        submitButton.addActionListener(
        
            new ActionListener(){
                
                @Override
                public void actionPerformed(ActionEvent event) {

                    ready = true;
                    jFrame.dispose();
                    
                }     
                
            }
        
        );
        jFrame.add(submitButton);
        
        jFrame.setVisible(true);
        jFrame.setLocation(500, 500);
        jFrame.setResizable(false);
        
    }
    
    public String getPassword(){
        
        return new String(passwordField.getPassword());
        
    }
    
    public String getUsername(){
        
        return usernameField.getText();
        
    }
    
    public void setMessageLabel(String text){
        
        messageLabel.setText(text);
        jFrame.repaint();
        
    }
    
    public void waitUntilReady(){
        
        Object waitObject = new Object();
        
        synchronized(waitObject){
            
            while(!ready){
                
                try{
                    
                    waitObject.wait(1);
                
                }catch(InterruptedException interruptedException){
                    
                    interruptedException.printStackTrace();
                    
                }
                
            }
            
        }
        
    }
    
}

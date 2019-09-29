package graphics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class NewUserRegister {
    
    public JFrame jFrame;
    private JTextField usernameField;
    private JTextField passwordFieldOne;
    private JPasswordField passwordFieldTwo;
    private JLabel label;
    private JLabel alertLabel;
    private JButton submitButton;
    
    private boolean isReady = false;
    
    public NewUserRegister(){
        
        jFrame = new JFrame("Register New User");
        jFrame.setSize(400, 160);
        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jFrame.setLayout(null);
        
        alertLabel = new JLabel();
        alertLabel.setLocation(150, 2);
        alertLabel.setSize(300, 15);
        alertLabel.setForeground(Color.red);
        jFrame.add(alertLabel);
        
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
        
        passwordFieldOne = new JTextField();
        passwordFieldOne.setColumns(15);
        passwordFieldOne.setSize(passwordFieldOne.getPreferredSize());
        passwordFieldOne.setLocation(150, 50);
        passwordFieldOne.setToolTipText("Enter Password");
        jFrame.add(passwordFieldOne);
        
        label = new JLabel("Reenter Password: ");
        label.setLocation(10, 80);
        label.setSize(label.getPreferredSize());
        jFrame.add(label);
        
        passwordFieldTwo = new JPasswordField();
        passwordFieldTwo.setColumns(15);
        passwordFieldTwo.setSize(passwordFieldTwo.getPreferredSize());
        passwordFieldTwo.setLocation(150, 80);
        passwordFieldTwo.setToolTipText("Enter Password");
        jFrame.add(passwordFieldTwo);
        
        submitButton = new JButton("Submit");
        submitButton.setLocation(185, 105);
        submitButton.setSize(100, 20);
        submitButton.addActionListener(
        
            new ActionListener(){
                
                @Override
                public void actionPerformed(ActionEvent event){
                    
                    isReady = true;
                    
                }
                
            }
        
        );
        jFrame.add(submitButton);
        
        jFrame.setVisible(true);
        jFrame.setLocation(500, 500);
        jFrame.setResizable(false);
        
    }
    
    public boolean passwordsMatch(){
        
        return passwordFieldOne.getText().equals(new String(passwordFieldTwo.getPassword()));
        
    }
    
    public String getPassword(){
        
        return new String(passwordFieldTwo.getPassword());
        
    }
    
    public String getUsername(){
        
        return usernameField.getText();
        
    }
    
    public void setAlertLabelText(String text){
        
        alertLabel.setText(text);
        jFrame.repaint();
        
    }
    
    public void waitUntilReady(){
        
        Object waitObject = new Object();
        
        synchronized(waitObject){
            
            while(!isReady){
                
                try{
                    
                    waitObject.wait(1);
                
                }catch(InterruptedException interruptedException){
                    
                    interruptedException.printStackTrace();
                    
                }
                
            }
            
        }
        
    }
    
    public boolean isValid(){
        
        return !getUsername().contains(" ");
        
    }
    
}

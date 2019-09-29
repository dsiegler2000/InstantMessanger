package server;

import graphics.ServerGraphics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import userdata.Account;

public class ServerDriver{
    
    private static final Account accounts = new Account();
    private static final Server server = new Server();
    private static final ServerGraphics serverGraphics = new ServerGraphics();
    private static ListenerThread listenerThread = new ListenerThread();

    //private static final ExecutorService threadPool = Executors.newFixedThreadPool(15);
    
    public static void main(String[] args) throws InterruptedException {
        
        listenerThread.start();

        Account.initUserdata();
        
        serverGraphics.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        server.startRunning();
        
        //ASK ALL USERS TO LOG IN, DON'T DETECT NEW USERS
        
        //MAKE IT SPLIT MESSAGES ONTO A NEW LINE AFTER A CERTAIN
        //AMOUNT OF CHARACTERS
        
        //IMPLEMENT ACCOUNTS, LOGGING IN, AND SENDING IMAGES, 
        //SERVERS AS A ROUTER, AND GROUP CHATTING, AND SENDING IMAGES

    }
    
    public static Account getAccounts(){return accounts;}
    public static Server getServer(){return server;}
    public static ServerGraphics getServerGraphics(){return serverGraphics;}
    //public static ExecutorService getThreadPool(){return threadPool;}
    public static ListenerThread getListenerThread(){return listenerThread;}
    public static void setListenerThread(ListenerThread lt){listenerThread = lt;}
    
    //public static void execute(Runnable runnable){threadPool.execute(runnable);}
    
}

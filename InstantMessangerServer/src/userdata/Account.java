package userdata;

import java.io.File;

import java.util.ArrayList;

public class Account {

    public static final String USERDATA_FOLDER_LOCATION_ON_MACBOOK =
        "/Users/dsiegler19/NetBeansProjects/InstantMessangerServer/src/userdata/";
    public static final String USERDATA_FOLDER_LOCATION_ON_PC =
        "C:\\Users\\Dylan Siegler\\Documents\\NetBeansProjects\\InstantMessangerServer\\src\\userdata\\";

    private static File userdata = new File(USERDATA_FOLDER_LOCATION_ON_MACBOOK + "userdata.json");

    //arraylist of userdata.json
    private static ArrayList<User> usersInUserdata = new ArrayList<>();
    
    //arraylist of online.json
    private static ArrayList<User> usersOnline = new ArrayList<>();
    
    public Account(){
        
        usersInUserdata = User.getUsersFromUserdata();
        usersOnline = new ArrayList<>();
        
    }

    /**NEEDS TO BE IMPLEMENTED*/
    public static void initUserdata(){

        

    }
    
    //returns false if there is already a user with that username
    public static boolean checkIfValidUser(String username){
        
        for(int i = 0; i < usersInUserdata.size(); i++){
            
            if(usersInUserdata.get(i).getUsername().equals(username)){
                
                return false;
                        
            }
            
        }
        
        return true;
        
    }
    
    //removes the user from the online arraylist
    public boolean removeUserFromOnline(User u){
        
        return usersOnline.remove(u);
        
    }
    
    //add a user from the online arraylist
    public boolean addUserToOnline(User u){
        
        return usersOnline.add(u);
        
    }
    
    public void setUsersInUserdata(ArrayList<User> newUserdata){
        
        usersInUserdata = newUserdata;
        
    }
    
    public ArrayList<User> getUsersInUserdata(){
        
        return usersInUserdata;
        
    }

}

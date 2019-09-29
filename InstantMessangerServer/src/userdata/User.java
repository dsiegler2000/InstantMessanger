package userdata;

import com.google.gson.Gson;
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

public class User {

    private final String username;
    private final String password;
    
    ArrayList<String> friends;
    
    private static Gson gson = new Gson();
        
    private static File userdata = new File(Account.USERDATA_FOLDER_LOCATION_ON_MACBOOK + "userdata.json");
    
    public User(){
        
        username = "";
        password = "";
        friends = new ArrayList<>();
        
    }
    
    public User(String username, String password){
        
        this.username = username;
        this.password = password;
                
    }
    
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public ArrayList<String> getFriends(){return friends;}
    
    public void addFriend(String friendsUsername){friends.add(friendsUsername);}
    public boolean isAFriend(String friendsUsername){return friends.contains(friendsUsername);}
    
    public void addUserToUserdata(){
                
        try{
            
            FileWriter fw = new FileWriter(userdata.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            String json = gson.toJson(this);
            bw.append(json + "\n");
            bw.close();
            fw.close();
        
        }catch(IOException ioException){
            
            ioException.printStackTrace();
            
        }
        
    }
    
    //returns all of the users in userdata.json
    public static ArrayList<User> getUsersFromUserdata(){
        
        ArrayList<User> users = new ArrayList<>();
        String currentLine;
        
        try{
        
            FileReader fr = new FileReader(userdata.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);

            while((currentLine = br.readLine()) != null){

                users.add(gson.fromJson(currentLine, User.class));

            }
            
            fr.close();
            br.close();
        
        }catch(IOException ioException){
                
            ioException.printStackTrace();
                
        }
        
        return users;
        
    }
    
}

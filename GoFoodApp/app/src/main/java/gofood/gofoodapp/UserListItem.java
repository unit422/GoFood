package gofood.gofoodapp;

import java.io.Serializable;

/**
 * Created by dhoy__000 on 6/8/2018.
 */

public class UserListItem implements Serializable {
    private String name,ID,userType,userUID;
    //private int ;

    public UserListItem(String name, String ID, String userType,String userUID){
        this.name = name;
        this.ID = ID;
        this.userType = userType;
        this.userUID = userUID;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setID(String ID){
        this.ID = ID;
    }

    public void setUserType(String userType){
        this.userType= userType;
    }

    public void setUserUID(String userUID){
        this.userUID= userUID;
    }

    public String getID() {
        return ID;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }

}

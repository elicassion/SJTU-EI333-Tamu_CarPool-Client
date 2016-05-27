package com.ultron.tamu_carpool.usr;

import java.io.Serializable;

/**
 * Created by admin on 2016/5/19.
 */
public class User implements Serializable {
    public int userType;
    public String ID;

    public User(String id, int usertype){
        ID = id;
        userType = usertype;
    }
    //TODO: using string information to initialize
    //TODO: get information into strings
}

package com.ultron.tamu_carpool.util;

import android.util.Log;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Time;

public class InteractUtil {
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "18202121850:qwerty"
    };
    private Socket socket = null;
    private String message;
    private static final String serverIP = "192.168.3.15";
    private String mUserID = null;
    private static final int serverPort = 54321;
    private static final String SOCKET_ERROR = "socket error";
    public boolean socketSuccess = true;

    private PrintWriter send = null;
    private BufferedReader back = null;

    public InteractUtil(){
        try {
            socket = new Socket(serverIP, serverPort);
            send = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
            back = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketSuccess = true;
        }catch (Exception e) {
            Log.e(SOCKET_ERROR, e.toString());
            socketSuccess = false;
        }
    }

    public void setUserID(String userID){
        if (mUserID != null) return;
        mUserID = userID;
    }

    public boolean checkIDPassword(String userID, String password){

//        for (String credential : DUMMY_CREDENTIALS) {
//            String[] pieces = credential.split(":");
//            if (pieces[0].equals(userID)) {
//                // Account exists, return true if the password matches.
//                return pieces[1].equals(password);
//            }
//        }
        try {
            JSONObject jIdPw = new JSONObject();
            jIdPw.put("id", userID);
            jIdPw.put("pw", password);
            send.println(jIdPw.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            boolean success = jBackInfo.getBoolean("success");
            Log.e("success: ", backInfo);
            return success;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    public int getUserType(){
        return 1;
    }

    public boolean getState(){
        return false;
    }

    public int getMaxPassenger(){
        return 1;
    }

    public int getReputation(){
        return 5;
    }

    public String match(DriveRouteResult result, int toolType, Time time){
        String mMatchResult = "{" +
                "    \"users\" : "+
                "["+
                "{"+
                "  \"phone\" : \"18217209315\","+
                "  \"repu\" : 5"+
                "},"+
                "{"+
                "  \"phone\" : \"11111111111\","+
                "  \"repu\" : 1"+
                "},"+
                "{"+
                "  \"phone\" : \"22222222222\","+
                "  \"repu\" : 2"+
                "},"+
                "{"+
                "  \"phone\" : \"33333333333\","+
                "  \"repu\" : 3"+
                "},"+
                "{"+
                "  \"phone\" : \"44444444444\","+
                "  \"repu\" : 4"+
                "},"+
                "{"+
                "  \"phone\" : \"00000000000\","+
                "  \"repu\" : 0"+
                "}"+
                "]"+
                "}";
        return mMatchResult;
    }
}

package com.ultron.tamu_carpool.util;

import android.util.Log;

import com.amap.api.maps2d.model.RuntimeRemoteException;
import com.amap.api.services.core.LatLonPoint;
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
    private enum COMMAND{
        LOGIN(0), GET_USERTYPE(1), UPDATE_LOCATION(2);
        private int nCode;

        // 构造函数，枚举类型只能为私有

        private COMMAND(int _nCode) {

            this.nCode = _nCode;

        }
    }
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
            jIdPw.put("command",COMMAND.LOGIN);
            jIdPw.put("id", userID);
            jIdPw.put("password", password);
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
        try {
            JSONObject jType = new JSONObject();
            jType.put("command",COMMAND.GET_USERTYPE);
            jType.put("id", mUserID);
            send.println(jType.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            int type = jBackInfo.getInt("type");
            //Log.e("type: ", Integer.toString(type));
            return type;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
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

    public void updateLocation(LatLonPoint location){
        try{
            JSONObject jLoc = new JSONObject();
            jLoc.put("command", COMMAND.UPDATE_LOCATION);
            jLoc.put("latitude", location.getLatitude());
            jLoc.put("longitude", location.getLongitude());
            send.println(jLoc.toString());
        }catch(Exception e){throw new RuntimeException(e);}
    }
}

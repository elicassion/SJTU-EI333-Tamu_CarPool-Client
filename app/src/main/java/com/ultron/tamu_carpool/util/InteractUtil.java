package com.ultron.tamu_carpool.util;

import android.util.Log;

import com.amap.api.maps2d.model.RuntimeRemoteException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.ultron.tamu_carpool.usr.User;

import org.json.JSONArray;
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
        LOGIN(0), GET_USERTYPE(1), UPDATE_LOCATION(2), MATCH_CONFIRM(3),GET_CONFIRM(4);
        private int nCode;


        COMMAND(int _nCode) {

            this.nCode = _nCode;

        }
    }

    private Socket socket = null;
    private String message;
    private static final String serverIP = "192.168.3.28";
    private String mUserID = null;
    private static final int serverPort = 54321;
    private static final String SOCKET_ERROR = "socket error";
    public boolean socketSuccess = true;

    private PrintWriter send = null;
    private BufferedReader back = null;

    private static boolean mKnowed = false;

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
            jIdPw.put("command",COMMAND.LOGIN.nCode);
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
            jType.put("command",COMMAND.GET_USERTYPE.nCode);
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

    public boolean getState(User user){
        return false;
    }

    public int getMaxPassenger(User user){
        return 1;
    }

    public int getReputation(User user){
        return 5;
    }

    public String match(User user, DriveRouteResult result, int toolType, String time, String startName, String endName){
        //上传到服务器 保存本次请求 包括请求人、路径、类型、请求、起终点名字
        JSONObject jMatchResult = generate_test_match_result();


        return jMatchResult.toString();
    }

    private JSONObject generate_test_match_result() {
        JSONObject jMatchResult = new JSONObject();
        JSONArray jUsers = new JSONArray();
        JSONObject user1 = new JSONObject();
        JSONObject user2 = new JSONObject();
        JSONObject user3 = new JSONObject();
        JSONObject user4 = new JSONObject();
        JSONObject user5 = new JSONObject();
        JSONObject user6 = new JSONObject();
        JSONObject user7 = new JSONObject();
        JSONObject user8 = new JSONObject();
        try {
            user1.put("id", "11111111111");
            user1.put("reputation", 5);
            user1.put("distance", 100);
            JSONObject user1CarInfo = new JSONObject();
            user1CarInfo.put("number_plate", "ABC-200");
            user1CarInfo.put("car_color", "red");
            user1CarInfo.put("insurance", "$200000");
            user1.put("car_info", user1CarInfo);
            jUsers.put(user1);

            user2.put("id", "22222222222");
            user2.put("reputation", 5);
            user2.put("distance", 100);
            JSONObject user2CarInfo = new JSONObject();
            user2CarInfo.put("number_plate", "FGU-200");
            user2CarInfo.put("car_color", "red");
            user2CarInfo.put("insurance", "$200000");
            user2.put("car_info", user2CarInfo);
            jUsers.put(user2);

            user3.put("id", "33333333333");
            user3.put("reputation", 5);
            user3.put("distance", 100);
            JSONObject user3CarInfo = new JSONObject();
            user3CarInfo.put("number_plate", "AERY-200");
            user3CarInfo.put("car_color", "red");
            user3CarInfo.put("insurance", "$200000");
            user3.put("car_info", user1CarInfo);
            jUsers.put(user3);

            user4.put("id", "44444444444");
            user4.put("reputation", 5);
            user4.put("distance", 100);
            JSONObject user4CarInfo = new JSONObject();
            user4CarInfo.put("number_plate", "QWRQWR-200");
            user4CarInfo.put("car_color", "red");
            user4CarInfo.put("insurance", "$200000");
            user4.put("car_info", user1CarInfo);
            jUsers.put(user4);

            user5.put("id", "55555555555");
            user5.put("reputation", 5);
            user5.put("distance", 100);
            JSONObject user5CarInfo = new JSONObject();
            user5CarInfo.put("number_plate", "SDCAS-235");
            user5CarInfo.put("car_color", "red");
            user5CarInfo.put("insurance", "$200000");
            user5.put("car_info", user5CarInfo);
            jUsers.put(user5);

            user6.put("id", "66666666666");
            user6.put("reputation", 5);
            user6.put("distance", 100);
            JSONObject user6CarInfo = new JSONObject();
            user6CarInfo.put("number_plate", "ZXC-123");
            user6CarInfo.put("car_color", "red");
            user6CarInfo.put("insurance", "$200000");
            user6.put("car_info", user6CarInfo);
            jUsers.put(user6);

            user7.put("id", "77777777777");
            user7.put("reputation", 5);
            user7.put("distance", 100);
            JSONObject user7CarInfo = new JSONObject();
            user7CarInfo.put("number_plate", "ZDV212");
            user7CarInfo.put("car_color", "red");
            user7CarInfo.put("insurance", "$200000");
            user7.put("car_info", user7CarInfo);
            jUsers.put(user7);

            user8.put("id", "88888888");
            user8.put("reputation", 5);
            user8.put("distance", 100);
            JSONObject user8CarInfo = new JSONObject();
            user8CarInfo.put("number_plate", "ABC-200");
            user8CarInfo.put("car_color", "red");
            user8CarInfo.put("insurance", "$200000");
            user8.put("car_info", user8CarInfo);
            jUsers.put(user8);

            jMatchResult.put("users",jUsers);
            return jMatchResult;
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public void updateLocation(LatLonPoint location){
        try{
            JSONObject jLoc = new JSONObject();
            jLoc.put("command", COMMAND.UPDATE_LOCATION.nCode);
            jLoc.put("latitude", location.getLatitude());
            jLoc.put("longitude", location.getLongitude());
            send.println(jLoc.toString());
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public void matchConfirm(User user, User target){
        try{
            JSONObject jMatchConfirm = new JSONObject();
            jMatchConfirm.put("command", COMMAND.MATCH_CONFIRM.nCode);
            JSONObject jUser = new JSONObject();
            jUser.put("id", user.ID);
            jUser.put("usertype", user.userType);
            jMatchConfirm.put("user", jUser);
            JSONObject jTarget = new JSONObject();
            jTarget.put("id", target.ID);
            jTarget.put("usertype", target.userType);
            jMatchConfirm.put("target", jTarget);

            //send.println(jMatchConfirm);
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public boolean getMatchConfirm(User user){
//        try {
//            JSONObject jGetConfirm = new JSONObject();
//            jGetConfirm.put("command", COMMAND.GET_CONFIRM.nCode);
//            JSONObject jUser = new JSONObject();
//            jUser.put("id", user.ID);
//            jUser.put("usertype", user.userType);
//            jGetConfirm.put("user", jUser);
//            send.println(jGetConfirm.toString());
//            String backInfo = back.readLine();
//            JSONObject jBackInfo = new JSONObject(backInfo);
//            boolean success = jBackInfo.getBoolean("success");
//            return success;
//        }catch(Exception e){throw new RuntimeException(e);}
        return !mKnowed;
    }

    public void setKnowed(User user, boolean knowed){
        //TODO:modify server
        mKnowed = knowed;
    }
}

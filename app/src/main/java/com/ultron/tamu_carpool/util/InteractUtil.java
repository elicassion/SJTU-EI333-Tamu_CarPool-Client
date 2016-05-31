package com.ultron.tamu_carpool.util;

import android.util.Log;

import com.amap.api.maps2d.model.RuntimeRemoteException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InteractUtil {
    private enum COMMAND{
        LOGIN(0), GET_USERTYPE(1), UPDATE_LOCATION(14), MATCH_CONFIRM(13),GET_CONFIRM(15), MATCH(12),
        GET_ORDER_INFO(18), GET_PERSONAL_INFO(20), REMATCH(16), CONFIRM_ARRIVE(17), ADD_COMMENT(19);
        private int nCode;


        COMMAND(int _nCode) {

            this.nCode = _nCode;

        }
    }


    private static final String serverIP = "192.168.3.15";
    private static String mUserID = null;
    private static int mUserType = 0;
    private static final int serverPort = 54321;
    private static final String SOCKET_ERROR = "socket error";
    public static boolean socketSuccess = true;
    private static Socket socket = null;

    private static PrintWriter send = null;
    private static BufferedReader back = null;


    public InteractUtil(){
        try {
            if (socket == null) {
                socket = new Socket(serverIP, serverPort);
                send = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                back = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                socketSuccess = true;
            }
        }catch (Exception e) {
            Log.e(SOCKET_ERROR, e.toString());
            socketSuccess = false;
        }
    }

    public void setUserID(String userID){
        if (mUserID != null) return;
        mUserID = userID;
    }

    public int checkIDPassword(String userID, String password){

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
            int userType = jBackInfo.getInt("user_type");
            Log.e("success: ", backInfo);
            if (success){
                mUserID = userID;
                mUserType = userType;
                return userType;
            }
            else return 0;
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
         try {
            JSONObject jState = new JSONObject();
            jState.put("command",2);
            jState.put("id", mUserID);
            send.println(jState.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            boolean state = jBackInfo.getBoolean("state");
            return state;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public int getMaxPassenger(User user){
        try {
            JSONObject jMaxPassenger = new JSONObject();
            jMaxPassenger.put("command",3);
            jMaxPassenger.put("id", mUserID);
            send.println(jMaxPassenger.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            int maxpassenger = jBackInfo.getInt("maxpassenger");
            return maxpassenger;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public double getReputation(User user){
        try {
            JSONObject jReputation = new JSONObject();
            jReputation.put("command",4);
            jReputation.put("id", mUserID);
            send.println(jReputation.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            double reputation = jBackInfo.getDouble("reputation");
            return reputation;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean getGender(){
        try {
            JSONObject jGender = new JSONObject();
            jGender.put("command",5);
            jGender.put("id", mUserID);
            send.println(jGender.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            boolean gender = jBackInfo.getBoolean("gender");
            return gender;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getName(){
        try {
            JSONObject jName = new JSONObject();
            jName.put("command",6);
            jName.put("id", mUserID);
            send.println(jName.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            String name = jBackInfo.getString("name");
            return name;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getPhone(){
        try {
            JSONObject jPhone = new JSONObject();
            jPhone.put("command",7);
            jPhone.put("id", mUserID);
            send.println(jPhone.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            String phone = jBackInfo.getString("phone");
            return phone;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getNumber_Plate(){
        try {
            JSONObject jNumber_Plate = new JSONObject();
            jNumber_Plate.put("command",8);
            jNumber_Plate.put("id", mUserID);
            send.println(jNumber_Plate.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            String number_plate = jBackInfo.getString("number_plate");
            return number_plate;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getCarType(){
        try {
            JSONObject jCarType = new JSONObject();
            jCarType.put("command",9);
            jCarType.put("id", mUserID);
            send.println(jCarType.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            String cartype = jBackInfo.getString("cartype");
            return cartype;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public int getAge(){
        try {
            JSONObject jAge = new JSONObject();
            jAge.put("command",10);
            jAge.put("id", mUserID);
            send.println(jAge.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            int age = jBackInfo.getInt("age");
            return age;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public int getOrderNumber(){
        try {
            JSONObject jOrderNumber = new JSONObject();
            jOrderNumber.put("command",11);
            jOrderNumber.put("id", mUserID);
            send.println(jOrderNumber.toString());
            String backInfo = back.readLine();
            JSONObject jBackInfo = new JSONObject(backInfo);
            int order_number = jBackInfo.getInt("order_number");
            return order_number;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String reMatch(int queryNumber){
        try {
            JSONObject jReMatch = new JSONObject();
            jReMatch.put("command", COMMAND.REMATCH.nCode);
            jReMatch.put("query_number", queryNumber);
            send.println(jReMatch.toString());
            String backInfo = back.readLine();
            return backInfo;
        }catch(Exception e){throw new RuntimeException(e);}

    }

    public String match(User user, DriveRouteResult result, int poolType, String time, String startName, String endName, LatLonPoint startPoint, LatLonPoint endPoint){
        //上传到服务器 保存本次请求 包括请求人、路径、类型、请求、起终点名字
        DrivePath path = result.getPaths().get(0);
        List<DriveStep> steps = path.getSteps();
        Set<LatLonPoint> points = new HashSet<LatLonPoint>();
        for (int i = 0; i < steps.size(); ++i){
            DriveStep step = steps.get(i);
            List<LatLonPoint> stepPoint = step.getPolyline();
            points.addAll(stepPoint);
        }
        //test
        Log.e("points number: ", Integer.toString(points.size()));
        try {
            JSONObject jMatchQuery = new JSONObject();
            jMatchQuery.put("command", COMMAND.MATCH.nCode);
            JSONObject jUser = new JSONObject();
            jUser.put("id", user.getID());
            jUser.put("user_type", user.getUserType());
            JSONArray jPoints = new JSONArray();
            for (Iterator iterator = points.iterator(); iterator.hasNext(); ) {
                JSONObject jPoint = new JSONObject();
                LatLonPoint point = (LatLonPoint) iterator.next();
                jPoint.put("lat", point.getLatitude());
                jPoint.put("lon", point.getLongitude());
                jPoints.put(jPoint);
            }
            JSONObject jStartPoint = new JSONObject();
            jStartPoint.put("lat", startPoint.getLatitude());
            jStartPoint.put("lon", startPoint.getLongitude());
            JSONObject jEndPoint = new JSONObject();
            jEndPoint.put("lat", endPoint.getLatitude());
            jEndPoint.put("lon", endPoint.getLongitude());

            jMatchQuery.put("user", jUser);//object
            jMatchQuery.put("points", jPoints);//array
            jMatchQuery.put("pool_type", poolType);
            jMatchQuery.put("time", time);
            jMatchQuery.put("start_name", startName);
            jMatchQuery.put("end_name", endName);
            jMatchQuery.put("start_point", jStartPoint);//object
            jMatchQuery.put("end_point", jEndPoint);//object
            send.println(jMatchQuery.toString());
            String backInfo = null;
            backInfo = back.readLine();
            Log.e("match query: ", jMatchQuery.toString());
            Log.e("match result: ", backInfo);
            return backInfo;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


    public void updateLocation(User user, LatLonPoint location){
        try{
            JSONObject jLoc = new JSONObject();
            jLoc.put("command", COMMAND.UPDATE_LOCATION.nCode);
            jLoc.put("lat", location.getLatitude());
            jLoc.put("lon", location.getLongitude());
            jLoc.put("id", user.getID());
            send.println(jLoc.toString());
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public void matchConfirm(User user, User target, int selfQueryNumber, int targetQueryNumber){
        try{
            JSONObject jMatchConfirm = new JSONObject();
            jMatchConfirm.put("command", COMMAND.MATCH_CONFIRM.nCode);
            JSONObject jUser = new JSONObject();
            jUser.put("id", user.ID);
            jUser.put("user_type", user.userType);
            jMatchConfirm.put("user", jUser);
            JSONObject jTarget = new JSONObject();
            jTarget.put("id", target.ID);
            jTarget.put("user_type", target.userType);
            jMatchConfirm.put("target", jTarget);
            jMatchConfirm.put("self_query_number", selfQueryNumber);
            jMatchConfirm.put("target_query_number",targetQueryNumber);
            send.println(jMatchConfirm);
            Log.e("match confirm", jMatchConfirm.toString());
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public String getMatchConfirm(User user){
        try {
            JSONObject jGetConfirm = new JSONObject();
            jGetConfirm.put("command", COMMAND.GET_CONFIRM.nCode);
            JSONObject jUser = new JSONObject();
            jUser.put("id", user.ID);
            jUser.put("user_type", user.userType);
            jGetConfirm.put("user", jUser);
            send.println(jGetConfirm.toString());
            String backInfo = back.readLine();
            Log.e("back code", backInfo);
            return backInfo;
            //return null;
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public String getOrderInfo(User user){
        try {
            JSONObject jGetOrderInfo = new JSONObject();
            jGetOrderInfo.put("command", COMMAND.GET_ORDER_INFO.nCode);
            jGetOrderInfo.put("id", user.getID());
            jGetOrderInfo.put("user_type", user.getUserType());
            //jGetOrderInfo.put("type", type);
            send.println(jGetOrderInfo.toString());
            String backInfo = back.readLine();
            Log.e("orderinfo", backInfo);
            return backInfo;
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public void confirmArrive(int orderNumber){
        try{
            JSONObject jConfirmArrive = new JSONObject();
            jConfirmArrive.put("command", COMMAND.CONFIRM_ARRIVE.nCode);
            jConfirmArrive.put("order_number", orderNumber);
            jConfirmArrive.put("id", mUserID);
            send.println(jConfirmArrive.toString());
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public void addComment(User user, int orderNumber, String commentContent, double repu){
        try{
            JSONObject jAddComment = new JSONObject();
            jAddComment.put("command", COMMAND.ADD_COMMENT.nCode);
            jAddComment.put("order_number", orderNumber);
            jAddComment.put("id", user.getID());
            jAddComment.put("user_type", user.getUserType());
            jAddComment.put("reputation", repu);
            jAddComment.put("comment", commentContent);
            send.println(jAddComment.toString());
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public String getPersonalInfo(User user){
        try{
            JSONObject jGetPersonalInfo = new JSONObject();
            jGetPersonalInfo.put("command", COMMAND.GET_PERSONAL_INFO.nCode);
            jGetPersonalInfo.put("id", user.getID());
            jGetPersonalInfo.put("user_type", user.getUserType());
            send.println(jGetPersonalInfo.toString());
            String backInfo = back.readLine();
            return backInfo;
        }catch(Exception e){throw new RuntimeException(e);}
    }



}

package com.ultron.tamu_carpool.usr;

import com.amap.api.services.route.DrivePath;

/**
 * Created by admin on 2016/5/19.
 */
public class Passenger extends User{
    public Passenger(int _userType, String _id, DrivePath _path, int _state){
        mUserType = _userType;
        mId = _id;
        mPath = _path;
        mState = _state;
    }
}

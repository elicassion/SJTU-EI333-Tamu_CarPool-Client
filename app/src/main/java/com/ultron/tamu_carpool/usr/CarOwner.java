package com.ultron.tamu_carpool.usr;
import com.amap.api.services.route.DrivePath;

/**
 * Created by admin on 2016/5/19.
 */
public class CarOwner extends User{
    private int MAX_PSG;
    public CarOwner(int _userType, String _id, DrivePath _path, int _state, int _max_psg){
        mUserType = _userType;
        mId = _id;
        mPath = _path;
        mState = _state;
        MAX_PSG = _max_psg;
    }
}

package com.ultron.tamu_carpool;

import android.support.v7.app.AppCompatActivity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.api.maps2d.MapsInitializer;

import com.ultron.tamu_carpool.login.LoginActivity;
import com.ultron.tamu_carpool.ctrlcenter.CtrlCenterActivity;
import com.ultron.tamu_carpool.usr.CarOwner;
import com.ultron.tamu_carpool.usr.Passenger;
import com.ultron.tamu_carpool.usr.User;
public class MainActivity extends AppCompatActivity {

    private String user_id;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {

            Thread.sleep(1000);

        } catch (InterruptedException e) { }
        Intent intentlogin = new Intent(MainActivity.this,LoginActivity.class);
        startActivityForResult(intentlogin, 1);
        //startActivity(new Intent(MainActivity.this,LoginActivity.class));
        //LoginActivity.finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2){
            int userType = data.getIntExtra("usertype", 1);
            int state = data.getIntExtra("state", 0);
            String id = data.getStringExtra("id");
            if(userType == 1){
                user = new Passenger(userType, id, null, state);
            }
            else{
                int max_psg = Integer.parseInt(data.getStringExtra("maxpsg"));
                user = new CarOwner(userType, id, null, state, max_psg);
            }
            Intent intentCtrlCenter = new Intent(MainActivity.this, CtrlCenterActivity.class);
            //TODO: relay user info

            startActivity(intentCtrlCenter);
        }
    }


}

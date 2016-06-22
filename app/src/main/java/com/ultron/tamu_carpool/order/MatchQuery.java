package com.ultron.tamu_carpool.order;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by admin on 2016/6/22.
 */
public class MatchQuery {
    private JSONObject jQuery;
    private String time;
    private Date date;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public void initByJSON(JSONObject query){
        try {
            jQuery = query;
            time = jQuery.getString("time");
            date = dateFormat.parse(time);
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public static final class ComparatorValues implements Comparator<MatchQuery>{
        @Override
        public int compare(MatchQuery mq1, MatchQuery mq2){
            try{
                Date d1 = mq1.getDate();
                Date d2 = mq2.getDate();
                if (d1.after(d2)){
                    return -1;
                }
                else if (d1.equals(d2)){
                    return 0;
                }
                else return 1;

            }catch(Exception e){throw new RuntimeException(e);}
        }
    }

    public String getTime(){
        return time;
    }

    public JSONObject getJQuery(){
        return jQuery;
    }
    public Date getDate(){
        return date;
    }


}

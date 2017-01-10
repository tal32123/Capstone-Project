package tk.talcharnes.intouch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Tal on 12/24/2016.
 */

public class Utility {
    private final static String LOG_TAG = Utility.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "tk.talcharnes.intouch.ACTION_DATA_UPDATED";
    public static final String ACTION_CALL_NOTIFICATION = "action_call";
    public static final String ACTION_SEND_TEXT = "action_send_text";
    public static final String ACTION_NOTIFICATION = "action_notification";


    public static String createStringFromArrayList(ArrayList<String> arrayList){
        JSONObject json = new JSONObject();
        try {
            json.put("uniqueArrays", new JSONArray(arrayList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    public static ArrayList<String> getArrayListFromJSONString(String stringreadfromsqlite) throws JSONException {
        JSONObject json = new JSONObject(stringreadfromsqlite);
        JSONArray items = json.optJSONArray("uniqueArrays");
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            String str_value = items.optString(i);  //<< jget value from jArray
            Log.d(LOG_TAG, str_value);
            arrayList.add(str_value);
        }
        return arrayList;
    }

    public static void updateWidgets(Context mContext) {

        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(mContext.getPackageName());
        mContext.sendBroadcast(dataUpdatedIntent);
    }

    public static long getTimeForNotification(int hour, int minutes, int am_pm){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.AM_PM, am_pm);
        return  calendar.getTimeInMillis();
    }

    public static PendingIntent createNotificationPendingIntent(
            String name,
            String number,
            String messageArrayListString,
            String contactID,
            String photo_uri,
            String actionType,
            Context mContext
            ){

        Intent alertIntent = new Intent(mContext, AlertReceiver.class);
        alertIntent.putExtra("name", name);
        alertIntent.putExtra("number", number);
        alertIntent.putExtra("messageList", messageArrayListString);
        alertIntent.putExtra("contactID", contactID.toString());
        alertIntent.putExtra("photo_uri", photo_uri);
        alertIntent.setAction(actionType);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, Integer.parseInt(contactID.toString()), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    public static void createNotifications(PendingIntent notificationPendingIntent, Context mContext, Long alarmTime, int frequencyMultiplier){

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY * Long.valueOf(frequencyMultiplier), notificationPendingIntent);

    }

}


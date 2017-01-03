package tk.talcharnes.intouch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {
    Context mContext;
    String name;
    String messageList;
    String number;
    String ACTION_NOTIFICATION;
    String ACTION_CALL_NOTIFICATION;
    String ACTION_SEND_TEXT;
    final static String LOG_TAG = NotificationReceiver.class.getSimpleName();
    public NotificationReceiver() {

        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "name = " + name + "number = " + number);
        mContext = context;
        Bundle extrasBundle = intent.getExtras();
        name = extrasBundle.getString("name");
        messageList = extrasBundle.getString("messageList");
        number = extrasBundle.getString("number");
        if (ACTION_SEND_TEXT.equals(intent.getAction())) {
            sendText();
        }
        if (ACTION_CALL_NOTIFICATION.equals(intent.getAction())){
            sendCall();
        }



        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void sendText(){


        //Turn string of all messages into an ArrayList in order to get one specific message at random
        ArrayList<String> messagesArrayList = null;
        try {
            messagesArrayList = Utility.getArrayListFromJSONString(messageList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Random rand = new Random();

        //todo the following may cause a bug if there are no messages in list
        int  n = rand.nextInt(messagesArrayList.size());



        String message = messagesArrayList.get(n);

        try {

            //send text message
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(mContext, "Message Sent",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {

            //If text message wasn't sent attempt to send text another way (through the user's text messaging app)
            // Most likely due to text message permissions not being accepted by user
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + number));  // This ensures only SMS apps respond
            intent.putExtra("sms_body", message);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }

    public void sendCall(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (callIntent.resolveActivity(mContext.getPackageManager()) != null) {
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(callIntent);
        }
    }
}
package com.rym.benjmaa.alzheimermate.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.rym.benjmaa.alzheimermate.Activities.AlarmReceiverActivity;

public class MedsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(checkInternetConnection(context)) {
            try {
                System.out.println("////////////////////////////////////////////////d5alt rak lel brodcast meds");
                Bundle bundle = intent.getExtras();
                String message = bundle.getString("alarm_message");

                Intent newIntent = new Intent(context, AlarmReceiverActivity.class);
                newIntent.putExtra("alarm_message", message);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            } catch (Exception e) {
                Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }



    }
    public static boolean checkInternetConnection(Context context)
    {
        try
        {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())
                return true;
            else
                return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}

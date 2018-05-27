package com.rym.benjmaa.alzheimermate.Services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rym.benjmaa.alzheimermate.Models.Medicament;
import com.rym.benjmaa.alzheimermate.Utils.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MedService extends Service {
    private static final int REQUEST_CODE = 192837;
    List<Medicament> medss;
    String url1;
    public MedService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent , int flags, int startId) {
        Log.v("MyService","MyService is Started");
       // setAlarm(h,n);
        return super.onStartCommand(intent, flags, startId);
    }

    private void setAlarm(String nom, String heure ,String image) {

        Calendar cal = Calendar.getInstance();
        // add 5 minutes to the calendar object
        cal.add(Calendar.MINUTE, 1);
        Intent intent = new Intent(getApplicationContext(), MedsReceiver.class);
        intent.putExtra("alarm_message", "Time to take your drug named : "+ nom);
        intent.putExtra("alarm_image", image);
        System.out.println(heure);
        System.out.println("héthéka el nbr");
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, intent,    PendingIntent.FLAG_UPDATE_CURRENT);
        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), convert(heure),sender);
    }
    private static long convert(String time) {

        long timeInMilliseconds= (long) 0.0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            System.out.println("time= "+time);
            Date mDate = sdf.parse(time);
            timeInMilliseconds =mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void makeJsonObjectRequest(String urlJsonObj) {

        String REQUEST_TAG = "com.androidAlzheimerMate.medRequest";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, (JSONObject) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                JSONArray js = null;
                JSONArray js11 = null;
                try {
                    js = response.getJSONArray("data");
                    js11 = response.getJSONArray("photos");
                    medss = new ArrayList<>();
                    for (int i = 0; i < js.length(); i++) {
                        // JSONObject item = js.getJSONObject(i);
                        Medicament med = new Medicament();
                        med.setNom(js.getJSONObject(i).getString("nom"));
                        med.setNbPrises(js.getJSONObject(i).getInt("nbr_prises"));
                        med.setHeures_prises(js.getJSONObject(i).getString("heure_prise"));

                        med.setImage_med(js11.getString(i));

                        medss.add(med);
                        System.out.println("Liste médicaments"+med.toString());
                        setAlarm(med.getNom(),med.getHeures_prises(),med.getImage_med());
                    }



                    if(medss.isEmpty()){

                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                System.out.println(error.getMessage());
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq, REQUEST_TAG);

    }
}

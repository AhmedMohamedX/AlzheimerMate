package com.rym.benjmaa.alzheimermate.Activities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.rym.benjmaa.alzheimermate.Fragments.FamilyFragment;
import com.rym.benjmaa.alzheimermate.Fragments.LoginFragment;
import com.rym.benjmaa.alzheimermate.Fragments.MapFragment;
import com.rym.benjmaa.alzheimermate.Fragments.MedFragment;
import com.rym.benjmaa.alzheimermate.R;
import com.rym.benjmaa.alzheimermate.Services.PositionBReceiver;


public class Main2Activity extends AppCompatActivity implements MedFragment.OnFragmentInteractionListener, FamilyFragment.OnFragmentInteractionListener{
private Intent alarm,alarm2 ;
private AlarmManager alarmManager;
private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        BottomBar bottomBar =  findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_map);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_proches:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerfrag,new FamilyFragment()).commit();
                        break;
                    case R.id.tab_map:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerfrag,new MapFragment()).commit();
                        break;
                    case R.id.tab_meds:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerfrag,new MedFragment()).commit();
                        break;
                    case R.id.tab_responsable:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerfrag,new LoginFragment()).commit();
                        break;

                }

            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {

            }
        });
        alarm = new Intent(this, PositionBReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmRunning) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

        }

      //  alarm2 = new Intent(this, MedService.class);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        super.onSaveInstanceState(outState);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

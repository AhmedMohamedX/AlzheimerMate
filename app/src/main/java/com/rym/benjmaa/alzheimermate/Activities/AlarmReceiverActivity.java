package com.rym.benjmaa.alzheimermate.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rym.benjmaa.alzheimermate.Models.CircleTransform;
import com.rym.benjmaa.alzheimermate.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class AlarmReceiverActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    TextView nommed;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_alarm_receiver);
        nommed=findViewById(R.id.AlarmNomMed);
        ImageView image=findViewById(R.id.AlarmImageMed);
        if (getIntent() != null && getIntent().hasExtra("alarm_message")) {
            String message = getIntent().getStringExtra("alarm_message");
            String medim = getIntent().getStringExtra("alarm_image");

            Picasso.with(this).load(medim).transform(new CircleTransform()).into(image);
            nommed.setText(message);
        }
        Button stopAlarm =  findViewById(R.id.stopAlarm);



                stopAlarm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mMediaPlayer.stop();
                        System.exit(0);

                    }

        });

        playSound(this, getAlarmUri());
    }
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}

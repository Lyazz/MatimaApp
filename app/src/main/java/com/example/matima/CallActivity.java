package com.example.matima;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;


public class CallActivity extends AppCompatActivity {


    DatabaseHandler db = new DatabaseHandler(this, 1);
Button answer,hangup;
TextView tw,twname;
    TextToSpeech myTTS;

    private CompositeDisposable disposables;
    private String number;

    private OngoingCall ongoingCall;




    SensorManager mySensorManager;
    Sensor myProximitySensor;
    private PowerManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        answer = findViewById(R.id.answer);
        hangup = findViewById(R.id.hangup);
        tw = findViewById(R.id.textView2);
        twname=findViewById(R.id.textView);


        mySensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);
        if (myProximitySensor == null) {
            System.out.println("No Proximity Sensor!");
        } else {
            mySensorManager.registerListener(proximitySensorEventListener,
                    myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }











        ongoingCall = new OngoingCall();
        number = Objects.requireNonNull(getIntent().getData()).getSchemeSpecificPart();
        int etat = getIntent().getIntExtra("USER_NAME",0);
        System.out.println("THE STATE IS"+etat);


        if(etat==9){
            answer.setVisibility(View.INVISIBLE);
        }


        number = number.replace("+213","0");
        tw.setText(number);


        System.out.println("the state"+ongoingCall.state.getValue().toString());



        twname.setText(db.getNameByNum(number));

        ImageView iv = findViewById(R.id.imageView1);

        if(db.isContact(number)){
            Bitmap myBitmap = db.getImageByNum(number);
            iv.setImageBitmap(myBitmap);
        }else{
         iv.setImageDrawable(getResources().getDrawable(R.drawable.uknown));
        }


        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    myTTS.setLanguage(Locale.FRENCH);
                    myTTS.speak(db.getNameByNum(number), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });





        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ongoingCall.answer();
            }
        });

        hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ongoingCall.hangup();
                finish();
            }
        });

    }


    SensorEventListener proximitySensorEventListener
            = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            WindowManager.LayoutParams params = CallActivity.this.getWindow().getAttributes();
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                    params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.screenBrightness = -1f;
                    getWindow().setAttributes(params);
                    System.out.println("Away");
                } else {
                    params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.screenBrightness = 0;
                    getWindow().setAttributes(params);
                    System.out.println("Near");
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
                    wl.acquire();
                }
            }
        }
    };


    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, CallActivity.class)
                .putExtra("USER_NAME", call.getState())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
    }
}

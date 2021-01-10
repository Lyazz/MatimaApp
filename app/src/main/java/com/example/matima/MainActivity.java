package com.example.matima;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.Person;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telecom.TelecomManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

public class MainActivity extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    DatabaseHandler db = new DatabaseHandler(this, 1);
    private Button settings;
   // ArrayList<String> arrayList;
    ArrayList<String> arrayListContact;

    private static final int PERMISSION_REQUEST_CODE = 1;
    MyAdapter adapter;
    private ImageView micButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        final List<Personne> personnes = db.getPersonnes();


        // arrayList = new ArrayList<>(); //empty array list.


        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

/*
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }else{

                arrayListContact = new ArrayList<>();
                arrayListContact = readContacts();

            for(int i=0; i < arrayListContact.size(); i++){
                System.out.println( arrayListContact.get(i) );
            }

        }


 */

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        micButton = findViewById(R.id.button);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                //editText.setText("");
                //editText.setHint("Listening...");
                System.out.println("Listening...");
                micButton.setImageResource(R.drawable.mic_standby);
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                micButton.setImageResource(R.drawable.mic_standby);
            }

            @Override
            public void onError(int i) {

            }



            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.mic_standby);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //editText.setText(data.get(0));
                System.out.println(data.get(0));

                int max = 0;
                String nomm=null;

                for(int i=0;i<personnes.size();i++){
                    System.out.println("Le ratio est de " +FuzzySearch.ratio(data.get(0),personnes.get(i).getNom()));
                    if(FuzzySearch.ratio(data.get(0),personnes.get(i).getNom()) > max ){
                        max = FuzzySearch.ratio(data.get(0),personnes.get(i).getNom());
                        nomm= personnes.get(i).getNom();
                    }
                }
                System.out.println("La personne maximale est "+nomm);




                Context context = getApplicationContext();
                Intent intent = new Intent(context, Main2Activity.class);

                intent.putExtra("NOMAPPELLE",nomm);
                intent.putExtra("NUMAPPELLE",nomm);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);

            }








            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                micButton.setImageResource(R.drawable.mic_standby);
            }
        });


        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.mic_onprogress);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });



        if(db.isEmpty()){
            Intent intent = new Intent(getApplicationContext(), Bienvenue.class);
            startActivity(intent);
            finish();
        }



        RecyclerView rw = (RecyclerView) findViewById(R.id.recyclerView);
        ImageView im = (ImageView) findViewById(R.id.bat);


        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.add_contact).withIcon(R.drawable.plus).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Context context = view.getContext();
                Intent intent = new Intent(context, AjouterPersonne.class);
                context.startActivity(intent);
                return true;
            }
        });
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.delete_contact).withIcon(R.drawable.remove).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Context context = view.getContext();
                Intent intent = new Intent(context, SupprimerPersonne.class);
                context.startActivity(intent);
                return true;
            }
        });
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.exit).withIcon(R.drawable.exit).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                System.out.println("quitter");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);

                return true;
            }
        });

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.blue)
                .addProfiles(
                        new ProfileDrawerItem().withName(R.string.credits).withEmail("yazid06@hotmail.co.uk").withIcon(getResources().getDrawable(R.drawable.settings))
                )

                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:yazid06@hotmail.co.uk"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.about);
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                        return false;
                    }
                })

                .build();

        Drawer result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)


                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3
                )

                .build();



        BatteryManager myBatteryManager = (BatteryManager) this.getSystemService(Context.BATTERY_SERVICE);




        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        System.out.println("batlevel"+batLevel);

        if((batLevel<=20)&&(batLevel>0)){
            im.setBackgroundResource(R.drawable.bat1);
        }
        else if((batLevel<=40)&&(batLevel>20)){
            im.setBackgroundResource(R.drawable.bat2);
        }
        else if((batLevel<=60)&&(batLevel>40)){
            im.setBackgroundResource(R.drawable.bat3);
        }
        else if((batLevel<=99)&&(batLevel>60)){
            im.setBackgroundResource(R.drawable.bat4);
        }
        else if(batLevel==100){
            im.setBackgroundResource(R.drawable.bat5);
        }


        if(myBatteryManager.isCharging()){
            im.setBackgroundResource(R.drawable.bat0);
        }



        List<Personne> list_personne = db.getPersonnes();


        rw.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new MyAdapter(this, list_personne);
        rw.setAdapter(adapter);





    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }


    /*
    private ArrayList<String> readContacts() {
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if (cursor.moveToFirst()){
            do {     arrayList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            }while (cursor.moveToNext());
            //arrayAdapter.notifyDataSetChanged();
        }

        return arrayList;
    }


     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //readContacts();
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }

        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }

















    @Override
    public void onResume(){
        super.onResume();



    }


    @Override
    protected void onPause() {
        super.onPause();


        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);


       // activityManager.moveTaskToFront(getTaskId(), 0);

        super.onPause();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean screenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }
        if (!screenOn) {    //Screen off by lock or power
           recreate();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {


        }
        return true;
    }

    public void makeCall(String s)
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + s));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            requestForCallPermission();

        } else {
            startActivity(intent);
        }
    }





    public void requestForCallPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE))
        {
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_CODE);
        }
    }


































    



}
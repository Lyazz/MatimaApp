package com.example.matima;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import android.telecom.Call;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

    DatabaseHandler db = new DatabaseHandler(this, 1);
    TextToSpeech myTTS;

    private static final int PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.add_contact).withIcon(R.drawable.plus).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Context context = view.getContext();
                Intent intent = new Intent(context, AjouterPersonne.class);
                context.startActivity(intent);
                return true;
            }
        });

        PrimaryDrawerItem item11 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.import_contacts).withIcon(R.drawable.importt).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ImporterContacts.class);
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
                        item11,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3
                )

                .build();

        Bundle extras = getIntent().getExtras();

        Intent intent = getIntent();

         Button agree= (Button) findViewById(R.id.agree);
         Button deny= (Button) findViewById(R.id.deny);

        final String nom= extras.getString("NOMAPPELLE");
        final String num= extras.getString("NUMAPPELLE");

        TextView tnom= findViewById(R.id.textView);
        TextView tnum= findViewById(R.id.textView2);
        ImageView icon = findViewById(R.id.icon);

        if(db.isViber(nom)) icon.setImageResource(R.drawable.viber2);
        else icon.setImageResource(R.drawable.telephone2);

        tnom.setText(""+nom+"");
        tnum.setText(""+num+"");


        ImageView iv = findViewById(R.id.imageView1);

        Bitmap myBitmap = db.getImage(nom);
        iv.setImageBitmap(myBitmap);



        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    myTTS.setLanguage(Locale.FRENCH);
                    myTTS.speak(nom, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });



        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                db.incrementCpt(nom);
                if(!db.isViber(nom)) makeCall(num);
                else {
                    String sphone = num;
                    Uri uri = Uri.parse("tel:" + Uri.encode(sphone));
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                    intent.setData(uri);
                    Context context = view.getContext();
                    context.startActivity(intent);
                }
            }
        });




        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }




    public void makeCall(String s)
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + s));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            requestForCallPermission();

        } else {
            startActivity(intent);

        }
    }
    public void requestForCallPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE))
        {
        }
        else {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }

}

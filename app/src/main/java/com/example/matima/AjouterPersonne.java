package com.example.matima;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;

public class AjouterPersonne extends AppCompatActivity {

     ImageView image;

    DatabaseHandler db = new DatabaseHandler(this, 1);

    private static final int REQUEST_ID = 1;
    private static final int HALF = 2;
    public static final String TAG = "YOUR-TAG-NAME";

    Uri uri;


    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_personne);


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




        Button button = findViewById(R.id.button);
        button.setText(R.string.add_contact);




        final EditText nomprenom =findViewById(R.id.nomprenom);
        nomprenom.setHint(R.string.name);
        final EditText numtel =findViewById(R.id.numtel);
        numtel.setHint(R.string.tel);
        image = findViewById(R.id.imgprofile);
        final CheckBox cb = findViewById(R.id.checkbox);
        cb.setText(R.string.viber_contact);
        isWriteStoragePermissionGranted();




        ((ImageView) findViewById(R.id.imgprofile)).setImageResource(R.drawable.blue);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String S_nomprenom = nomprenom.getText().toString();
                String S_numtel = numtel.getText().toString();


                if((S_nomprenom.isEmpty())||(S_numtel.isEmpty())||(image.getDrawable()==null)){
                    Toast toast = Toast. makeText(getApplicationContext(), "Un ou plusieurs champs manquant(s)", Toast. LENGTH_SHORT);
                    toast.show();
                }
                else{
                    ImageView imagee = findViewById(R.id.imgprofile);

                    BitmapDrawable drawable = (BitmapDrawable) imagee.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    Personne p = new Personne(S_nomprenom," ",S_numtel,bitmap,0);
                    p.setNom(S_nomprenom);
                    p.setNum(S_numtel);
                    p.setImage(bitmap);
                    if(cb.isChecked())  p.setType("viber");
                    else p.setType("phone");

                db.addPersonne(p);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.startPickImageActivity(AjouterPersonne.this);

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK){
            Uri imageuri = CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri)){
                uri=imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else {
                startCrop(imageuri);
            }
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                image.setImageURI(result.getUri());
                Bitmap bitmap = result.getBitmap();





                Toast.makeText(this,"Image update succesufuly",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

}

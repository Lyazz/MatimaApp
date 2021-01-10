package com.example.matima;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.Person;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImporterContacts extends AppCompatActivity {
    ArrayList<Personne> arrayList;
    ImageView imageView;
    String nom;
    String num;
    Bitmap imageBitmap;
    DatabaseHandler db = new DatabaseHandler(this, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importer_contacts);
        arrayList = new ArrayList<Personne>(); //empty array list.
        imageView = findViewById(R.id.imageview);


//checking whether the read contact permission is granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
// requesting to the user for permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);

        } else {
//if app already has permission this block will execute.
            readContacts();
        }

    }
    // if the user clicks ALLOW in dialog this method gets called.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        readContacts();
    }
    // function to read contacts using content resolver
    private ArrayList<Personne> readContacts() {
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        if (cursor.moveToFirst()){
            do {

                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                System.out.println("URI " +imageUri);
                 nom = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                 num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Picasso.get()
                        .load("content://com.android.contacts/contacts/354/photo")
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                imageView.setImageBitmap(bitmap);
                                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                                Bitmap bitmapp = drawable.getBitmap();
                                Personne p = new Personne(nom,nom,num,bitmapp,0);
                                db.addPersonne(p);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                /*
                if(imageBitmap==null)System.out.println("bitmap null");
                else {
                    System.out.println("bitmap not null");
                    Personne p = new Personne(nom,nom,num,imageBitmap,0);
                    db.addPersonne(p);
                    //arrayList.add(p);
                }


                 */
            }while (cursor.moveToNext());
        }


        return arrayList;
    }



}
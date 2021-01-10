package com.example.matima;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {






    private Activity activity;
    List<Personne> characters = Collections.emptyList();



    public MyAdapter(Activity activity,List<Personne> characters){
        this.activity=activity;
        this.characters=characters;
    }






    @Override
    public int getItemCount() {
        return characters.size();
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Context context = holder.itemView.getContext();

        DatabaseHandler db = new DatabaseHandler(context, 1);
        String nomm = characters.get(position).getNom();
        String numm = characters.get(position).getNum();
        Bitmap imgg = characters.get(position).getImage();
        holder.nom.setText(nomm);
        holder.num.setText(numm);
        holder.img.setImageBitmap(imgg);
        if(db.isViber(nomm))  holder.icon.setImageResource(R.drawable.viber2);
        else holder.icon.setImageResource(R.drawable.telephone2);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView nom;
        private final TextView num;
        private final ImageView img;
        private final ImageView icon;




        public MyViewHolder(final View itemView) {
            super(itemView);

            nom = ((TextView) itemView.findViewById(R.id.nom));
            num = ((TextView) itemView.findViewById(R.id.num));
            img = ((ImageView) itemView.findViewById(R.id.imageView));
            icon = ((ImageView) itemView.findViewById(R.id.icon));




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Context context = view.getContext();
                    Intent intent = new Intent(context, Main2Activity.class);
                    intent.putExtra("NOMAPPELLE",nom.getText().toString());
                    intent.putExtra("NUMAPPELLE",num.getText().toString());




                    /*
                    BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();


                    File path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);



                    //File myFile = new File(path,"yazid.png");




                    try (FileOutputStream out = new FileOutputStream(myFile)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("BitmapImage", myFile.getAbsolutePath());


                     */

                    context.startActivity(intent);
                }
            });
        }
    }






}

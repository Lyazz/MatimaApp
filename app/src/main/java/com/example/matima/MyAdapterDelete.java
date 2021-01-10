package com.example.matima;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapterDelete extends RecyclerView.Adapter<MyAdapterDelete.MyViewHolder> {



    private Activity activity;
    List<Personne> characters = Collections.emptyList();

    public MyAdapterDelete(Activity activity, List<Personne> characters){
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
        View view = inflater.inflate(R.layout.recyclerviewdelete_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String nomm = characters.get(position).getNom();
        String numm = characters.get(position).getNum();
        Bitmap imgg = characters.get(position).getImage();
        holder.nom.setText(nomm);
        holder.num.setText(numm);
        holder.img.setImageBitmap(imgg);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView nom;
        private final TextView num;
        private final ImageView img;




        public MyViewHolder(final View itemView) {
            super(itemView);

            nom = ((TextView) itemView.findViewById(R.id.nom));
            num = ((TextView) itemView.findViewById(R.id.num));
            img = ((ImageView) itemView.findViewById(R.id.imageView));
            Button btn = (Button) itemView.findViewById(R.id.delete);



            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Context context = view.getContext();
                    DatabaseHandler db = new DatabaseHandler(context, 1);
                    db.deletePersonne(nom.getText().toString());
                    Intent intent = new Intent(context, SupprimerPersonne.class);
                    context.startActivity(intent);



                }
            });
        }
    }




}

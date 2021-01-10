package com.example.matima;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.List;

public class SupprimerPersonne extends AppCompatActivity {
    DatabaseHandler db = new DatabaseHandler(this, 1);
    MyAdapterDelete adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supprimer_personne);


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


        RecyclerView rw = (RecyclerView) findViewById(R.id.recyclerView);

        List<Personne> list_personne = db.getPersonnes();
        rw.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new MyAdapterDelete(this, list_personne);
        rw.setAdapter(adapter);

    }


    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }


}

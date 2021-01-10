package com.example.matima;


import android.app.Person;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static String DB_Name = "MatimaDB.db";

    private static String TABLE_NAME1 = "personne";
    private static String COL1_eid = "eid";
    private static String COL1_nom = "nom";
    private static String COL1_num = "numero";
    private static String COL1_image = "image";
    private static String COL1_cpt = "cpt";
    private static String COL1_type="type";


    public DatabaseHandler(Context context, int version) {
        super(context, DB_Name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable1 = "create table " + TABLE_NAME1 + " (" + COL1_eid + " INTEGER  PRIMARY KEY AUTOINCREMENT , " + COL1_nom + " TEXT , " + COL1_num + " TEXT , " + COL1_image + " INTEGER, " + COL1_cpt + " INTEGER,"+ COL1_type +" TEXT);";
        db.execSQL(createTable1);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE if exists " + TABLE_NAME1);
        onCreate(db);
    }


    public List<Personne> getPersonnes() {
        List<Personne> list_personnes = new ArrayList<Personne>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + ".* from " + TABLE_NAME1 +" ORDER BY  "+TABLE_NAME1+"."+COL1_cpt+" DESC ", null);
        while (cr.moveToNext()) {
            String nom = cr.getString(1);
            String num = cr.getString(2);
            Bitmap image = getImage(cr.getBlob(3));
            int cpt = cr.getInt(4);
            Personne emp = new Personne(nom, "", num, image, cpt);
            emp.setNom(nom);
            emp.setNum(num);
            emp.setImage(image);
            emp.setCpt(cpt);
            list_personnes.add(emp);
        }
        cr.close();
        return list_personnes;
    }







    public Bitmap getImage(String nom) {
        Bitmap image = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + "."+COL1_image+" from " + TABLE_NAME1 + " WHERE "+COL1_nom+"='"+nom+"'", null);
        while (cr.moveToNext()) {
            image = getImage(cr.getBlob(0));
        }
        cr.close();
        return image;
    }

    public Bitmap getImageByNum(String num) {
        Bitmap image = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + "."+COL1_image+" from " + TABLE_NAME1 + " WHERE "+COL1_num+"='"+num+"'", null);
        while (cr.moveToNext()) {
            image = getImage(cr.getBlob(0));
        }
        cr.close();
        return image;
    }

    public String getNameByNum(String num) {
        String name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + "."+COL1_nom+" from " + TABLE_NAME1 + " WHERE "+COL1_num+"='"+num+"'", null);
        while (cr.moveToNext()) {
            name = cr.getString(0);
        }
        cr.close();
        return name;
    }

    public boolean isContact(String num) {
        String type = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + ".* from " + TABLE_NAME1 + " WHERE " + COL1_num + "='" + num + "' ", null);
        if (cr.getCount()==0) return false; else return true;

    }


    public void addPersonne(Personne pers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_nom, pers.getNom());
        cv.put(COL1_num, pers.getNum());
        cv.put(COL1_image, getBytes(pers.getImage()));
        cv.put(COL1_cpt, pers.getCpt());
        cv.put(COL1_type,pers.getType());
        db.insert(TABLE_NAME1, null, cv);

    }

    public void deletePersonne(String nom) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME1, "nom = ?", new String[]{nom + ""});
        System.out.println("Row " + nom + " deleted");
        //db.close();
    }

    public boolean isEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + ".* from " + TABLE_NAME1 +" ", null);

        if (cr.getCount()==0) return true; else return false;
    }






    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    public int getCpt(String nom) {
        int cpt=0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + "."+COL1_cpt+" from " + TABLE_NAME1 + " WHERE "+COL1_nom+"='"+nom+"'      ", null);
        while (cr.moveToNext()) {

            cpt = cr.getInt(0);

        }
        cr.close();
        return cpt;
    }

    public void incrementCpt(String nom){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = this.getCpt(nom);
        i++;
        String strSQL = "UPDATE "+TABLE_NAME1+" SET "+COL1_cpt+"='"+i+"' WHERE nom='"+nom+"' ";
        db.execSQL(strSQL);

    }


    public boolean isViber(String nom) {
        String type = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select " + TABLE_NAME1 + "." + COL1_type + " from " + TABLE_NAME1 + " WHERE " + COL1_nom + "='" + nom + "'      ", null);
        while (cr.moveToNext()) {
             type = cr.getString(0);
        }
        cr.close();
        if(type.equals("viber")) return true; else return false;
    }


    public void exportDB() {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "yazidbase.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor curCSV = db.rawQuery("select " + TABLE_NAME1 + ".* from " + TABLE_NAME1 + "", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }


    }


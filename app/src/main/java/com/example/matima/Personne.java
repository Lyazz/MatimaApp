package com.example.matima;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class Personne {
    String nom;
    String prenom;
    String num;
    Bitmap image;
    int cpt;
    String type;




    public Personne(String nom,String prenom, String num,Bitmap image,int cpt){
        nom=this.nom;
        prenom=this.prenom;
        num=this.num;
        image=this.image;
        cpt=this.cpt;
    }

    public Personne() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getCpt(){return cpt;}

    public void setCpt(int cpt){this.cpt=cpt;}

    public String getType(){return type;}

    public void setType(String type){this.type=type;}


}

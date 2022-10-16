package com.example.capturayguardaimagen;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;

public class ObjetoButtonImage {
    public File file = null;
    public String path = "";
    ImageButton imgBoton = null;

    Bitmap bitmap = null;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    ObjetoButtonImage(ImageButton button){
        this.imgBoton= button;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ImageButton getImgBoton() {
        return imgBoton;
    }

    public void setImgBoton(ImageButton imgBoton) {
        this.imgBoton = imgBoton;
    }
}

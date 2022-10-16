package com.example.capturayguardaimagen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    public ImageButton IBtn1,IBtn2,IBtn3,IBtn4,IBtn5,IBtn6;
    public Button btnSubir;

    private static final int REQUEST_PERMISSION_CAMERA = 101;
    private static final int REQUEST_IMAGE_CAMERA = 101;
    String currentPhotoPath;

    ObjetoButtonImage botonImg;
    Bitmap decoded;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        IBtn1 = (ImageButton) findViewById(R.id.imgBtn1);
        IBtn2 = (ImageButton) findViewById(R.id.imgBtn2);
        IBtn3 = (ImageButton) findViewById(R.id.imgBtn3);
        IBtn4 = (ImageButton) findViewById(R.id.imgBtn4);
        IBtn5 = (ImageButton) findViewById(R.id.imgBtn5);
        IBtn6 = (ImageButton) findViewById(R.id.imgBtn6);

        btnSubir = (Button)findViewById(R.id.btnSubir);

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity2.this, "Subir fotos", Toast.LENGTH_LONG).show();
            }
        });

        IBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonRef(IBtn1);
            }
        });
        IBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonRef(IBtn2);
            }
        });
        IBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonRef(IBtn3);
            }
        });

    }

    //====================aguas jaja    =====
    public void setButtonRef(ImageButton imageButton){
        botonImg = new ObjetoButtonImage(imageButton);
        //vemos si tenemos la api adecuada
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //verificamos si tenemos permisos de la camara
            //si tenemos permiso accedemos a la camara
            if(ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

                gotoCamera();
            }else{
                //sino solicitamos permiso
                // ===============================    #1
                ActivityCompat.requestPermissions(MainActivity2.this,new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);

            }

        }else{
            gotoCamera();
        }

    }


    //======================== #2
    //Obtenemos el permiso y lo mandamos a la camara
    //una vez se dispare el resultado de los permisos ejecutamos esto
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CAMERA){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //si le dio aceptar procedemos
                gotoCamera();
            }else{
                Toast.makeText(MainActivity2.this,"Por favor de acceso a la camara",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //=========================== #3
    //metodo que puede generar conflicto
    private void gotoCamera(){
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(camaraIntent.resolveActivity(getPackageManager()) != null){ //verificar si el dispositivo tiene camara
            //startActivityForResult(camaraIntent,REQUEST_IMAGE_CAMERA);//tenemos que ver el resultado de la camara

            //creamos el archivo
            File photoFile = null;
            try{
                //ObjetoButtonImage objetoFile = createFile();
                //currentPhotoPath = objetoFile.path;
                createFile();
                photoFile = botonImg.getFile();

/////////////////////////////////////////////////////////////////////////////////////// <- AQui
            }catch(IOException e){
                e.printStackTrace();
            }

            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(this,"com.example.capturayguardaimagen", photoFile);
                camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(camaraIntent,REQUEST_IMAGE_CAMERA);
            }

        }else{
            Toast.makeText(this, "No tienes camara?", Toast.LENGTH_LONG).show();
        }
    }
    // ================== #4

    //Actualizado (Ya no depende de una variable)

    private void createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss", Locale.getDefault()).format(new Date());
        String imgFileName = "IMG_" + timeStamp + "_"; //IMG_"FechaHora"_
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imgFileName,
                ".png",
                storageDir // se guardara en directorio pictures
        );
       // currentPhotoPath = image.getAbsolutePath();
        //aqui guardo un file y la absolutePath
        //ObjetoButtonImage objeto = new ObjetoButtonImage(image, image.getAbsolutePath());
        botonImg.setFile(image);
        botonImg.setPath(image.getAbsolutePath());
        //return objeto;
        //return image;
    }



    //================= #5
    //metodo que tiene una variable que cambia
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //aqui continuamos en gotoCamara

        if(requestCode == REQUEST_IMAGE_CAMERA){
            if(resultCode == Activity.RESULT_OK){ //si toma la foto

//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                imgView.setImageBitmap(bitmap);
//                Log.i("TAG","Result => " + bitmap);
                try {
                    File file = new File(botonImg.getPath());
                    Uri uri = Uri.fromFile(file);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            uri
                    );
                    setToImageView(getResizedBitmap(bitmap,1024));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //imgView.setImageURI(Uri.parse(currentPhotoPath));
            }else{
                //no tomo foto
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    //================= #6
    //metodo neutro
    private Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width <= maxSize && height <= maxSize){
            return bitmap;
        }

        float bitmapRatio = (float) width / (float) height;
        if(bitmapRatio > 1){
            width = maxSize;
            height = (int)(width / bitmapRatio);
        }else{
            height = maxSize;
            width = (int)(height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width,height,true);
    }

    //================== #7
    //metodo con 2 dos variables que cambiaran
    private void setToImageView(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        //decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        botonImg.setBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray())));
        //imgView.setImageBitmap(decoded);
        //IBtn1.setImageBitmap(decoded);
        botonImg.getImgBoton().setImageBitmap(botonImg.getBitmap());
    }


}



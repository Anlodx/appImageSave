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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
//lo que hacemos aqui es tomar foto y mantenerla privada en la carpeta android/data
public class MainActivity extends AppCompatActivity {
    public Button btnCaptura;
    public ImageView imgView;

    private static final int REQUEST_PERMISSION_CAMERA = 101;
    private static final int REQUEST_IMAGE_CAMERA = 101;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCaptura = (Button) findViewById(R.id.btnCaptura);
        imgView = (ImageView) findViewById(R.id.imgView);

        btnCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vemos si tenemos la api adecuada
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    //verificamos si tenemos permisos de la camara
                    //si tenemos permiso accedemos a la camara
                    if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        gotoCamera();
                    }else{
                        //sino solicitamos permiso
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);

                    }

                }else{
                    gotoCamera();
                }

            }
        });
    }

    //una vez se dispare el resultado de los permisos ejecutamos esto
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CAMERA){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //si le dio aceptar procedemos
                gotoCamera();
            }else{
                Toast.makeText(MainActivity.this,"Por favor de acceso a la camara",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //aqui continuamos en gotoCamara
        if(requestCode == REQUEST_IMAGE_CAMERA){
            if(resultCode == Activity.RESULT_OK){ //si toma la foto

//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                imgView.setImageBitmap(bitmap);
//                Log.i("TAG","Result => " + bitmap);
                imgView.setImageURI(Uri.parse(currentPhotoPath));
            }else{
                //no tomo foto
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoCamera(){
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(camaraIntent.resolveActivity(getPackageManager()) != null){ //verificar si el dispositivo tiene camara
            //startActivityForResult(camaraIntent,REQUEST_IMAGE_CAMERA);//tenemos que ver el resultado de la camara

            //creamos el archivo
            File photoFile = null;
            try{
                photoFile = createFile();

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

    private File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss", Locale.getDefault()).format(new Date());
        String imgFileName = "IMG_" + timeStamp + "_"; //IMG_"FechaHora"_
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imgFileName,
                ".png",
                storageDir // se guardara en directorio pictures
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
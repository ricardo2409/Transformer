package com.example.ricardotrevino.transformadores;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    ImageView ivFotoGrande;
    Bitmap bitmap, bOutput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ivFotoGrande = (ImageView)findViewById(R.id.ivFotoGrande);

        byte[] byteArray = getIntent().getByteArrayExtra("foto");

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //Rota la imagen para que se vea normal
        float degrees = 90;//rotation degree
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ivFotoGrande.setImageBitmap(bOutput);
    }
}

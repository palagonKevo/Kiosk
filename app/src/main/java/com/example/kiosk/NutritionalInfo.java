package com.example.kiosk;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NutritionalInfo extends AppCompatActivity {
    private String item;
    private TextView text;

    @Override
    protected  void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setTitle("Descripción");
        setContentView(R.layout.nutritional_info_layout);
        item = (String) getIntent().getSerializableExtra("productData");
        text = findViewById(R.id.textView);
        text.setText(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

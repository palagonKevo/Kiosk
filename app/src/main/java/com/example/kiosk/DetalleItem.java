package com.example.kiosk;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DetalleItem extends AppCompatActivity {

    private String item;
    private TextView desc;
    private ImageView img;
    private ConexionSQLiteHelper conn;
    private static final String API_KEY = "2ZTIGIeiwCV_I9TMo_C-ouYR9xULuOAsZKrjb0UWiSSE";
    private static final String URL = "https://api.eu-de.text-to-speech.watson.cloud.ibm.com/instances/93df280f-4be1-4a27-b211-850bf49e1b47";
    private final IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
    private TextToSpeech textToSpeech;
    private final String voice = "es-ES_LauraV3Voice";
    private MediaPlayer player;
    private Thread thread;
    private String description;
    private static final int BUFFER_SIZE = 1024;
    private static final int speechWpm = 250;

    @Override
    protected  void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setTitle("Descripción");
        setContentView(R.layout.detella_item);
        item = (String) getIntent().getSerializableExtra("objectData");
        desc = findViewById(R.id.desc);
        String id = item.split(" ")[0];
        img = findViewById(R.id.imageView2);
        conn = new ConexionSQLiteHelper(this);
        textToSpeech = new TextToSpeech(authenticator);
        textToSpeech.setServiceUrl(URL);
        initializeMediaPlayer();

        Cursor cursor = conn.getProductById(id);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            description = cursor.getString(1);
        }
        desc.setText(description);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(description);
                thread = new Thread(() -> {
                    try {
                        String fileName = description+".wav";
                        createSoundFile(description, voice,fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        });


    }
    private void initializeMediaPlayer() {
        player = new MediaPlayer();

        player.setOnBufferingUpdateListener((mp, percent) -> Log.i("Buffering", "" + percent));
    }

    public void startPlaying(String fileName) {
        try {
            player.reset();
            File file = new File(this.getFilesDir(), fileName);
            Uri fileUri = Uri.parse(file.getPath());
            player.setDataSource(this, fileUri);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(mp -> player.start());
            player.prepareAsync();
        } catch (IllegalArgumentException | SecurityException
                | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

    }
    public void createSoundFile(String text, String voice, String fileName) throws IOException {

        // synthesize the audio using
        SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                .text("<speak version=\"1.0\"><p><prosody rate=\"" + speechWpm + "\" pitch=\"-15%\"><s>" + text + "</s></prosody></p></speak>")
                .accept("audio/mp3")
                .voice(voice)
                .build();

        InputStream inputStream = textToSpeech.synthesize(synthesizeOptions).execute().getResult();
        InputStream in = WaveUtils.reWriteWaveHeader(inputStream);
        FileOutputStream fos = this.openFileOutput(fileName, Context.MODE_PRIVATE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = in.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        startPlaying(fileName);
        in.close();
        inputStream.close();
        Log.println(Log.INFO,"I","Synthesized and saved");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(thread!=null) thread.interrupt();
        if(player!=null) player.release();
    }
}

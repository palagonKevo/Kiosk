package com.example.kiosk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.databinding.FragmentSecondBinding;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private SpeechRecognizer speech;
    private TextView editText;
    private TextView rasaText;
    private UUID uuid;
    private static final String API_KEY = "2ZTIGIeiwCV_I9TMo_C-ouYR9xULuOAsZKrjb0UWiSSE";
    private static final String URL = "https://api.eu-de.text-to-speech.watson.cloud.ibm.com/instances/93df280f-4be1-4a27-b211-850bf49e1b47";
    private final IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
    private TextToSpeech textToSpeech;
    private final String voice = "es-ES_LauraV3Voice";
    private static final int BUFFER_SIZE = 1024;
    private String transcription;
    private static final int speechWpm = 250;
    private Intent speechRecognizerIntent;
    private MediaPlayer player;
    private Thread thread;
    private ListView table;
    private List<String> mList;
    private ArrayAdapter<String> mAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSecond.setOnClickListener(view1 -> NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment));

        editText = view.findViewById(R.id.text);
        rasaText = view.findViewById(R.id.rasa);
        editText.setText("");
        speech = SpeechRecognizer.createSpeechRecognizer(getContext());
        uuid = UUID.randomUUID();
        transcription = "";
        textToSpeech = new TextToSpeech(authenticator);
        textToSpeech.setServiceUrl(URL);
        table = view.findViewById(R.id.table);
        mList = new ArrayList<>();

        initializeMediaPlayer();

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");

        speech.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBeginningOfSpeech() {
                //editText.setText("");
                editText.setText("Listening...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                editText.setText("");
                // if recognition results on no match just ignore and listen again until it recognizes something
                if(error==7 || error==8){
                    speech.startListening(speechRecognizerIntent);
                } else {
                    Log.println(Log.ERROR,"E","Speech to text error: "+ error);
                }
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String input = data.get(0);
                System.out.println(input.toLowerCase(Locale.ROOT));
                //if(input.toLowerCase(Locale.ROOT).contains("hola")){
                    editText.setText(input.toLowerCase(Locale.ROOT));
                    //String[] strSplit = input.toLowerCase(Locale.ROOT).split("hola",2);
                    System.out.println("USER INPUT: "+input);
                    AsyncTask<String, Void, JSONArray> response = new RetrieveFeedTask().execute(input,uuid.toString());
                    String text = "";
                    try {
                        System.out.println(response.get());
                        JSONArray jsonArray = response.get();
                        for (int i = 0, size = jsonArray.length();i < size ; i++){
                            JSONObject item = jsonArray.getJSONObject(i);
                            System.out.println(item.toString());
                            if(item.has("text")) text+=item.getString("text");
                            if(item.has("custom")){
                                JSONObject product_list = new JSONObject(item.getString("custom"));
                                Iterator<String> keys = product_list.keys();
                                // clear the table before update it
                                table.setAdapter(null);
                                mList.clear();
                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    mList.add(product_list.getJSONObject(key).getString("product") + " " + product_list.getJSONObject(key).getString("units") + " "+ product_list.getJSONObject(key).getString("type_unit"));
                                    mAdapter = new ArrayAdapter<>(getContext(), R.layout.text_view_layout, mList);
                                    table.setAdapter(mAdapter);
                                }
                            }
                        }
                        transcription = text;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                System.out.println("TEXT: "+text);
                rasaText.setText(text);
                thread = new Thread(() -> {
                    if (transcription.length() > 0) {
                        try {
//                            List<String> words = new ArrayList<String>(Arrays.asList(transcription.split(" ")));
//                            // if the input text is too large, split it so there is no time out or excessive run time
//                            if(words.size()>MAX_WORDS){
//                                // resize the list in order to avoid failing some words
//                                if(words.size() % MAX_WORDS !=0) {
//                                    for (int i = 0; i < words.size() % MAX_WORDS; i++) {
//                                        words.add("");
//                                    }
//                                }
//                                for (int i = 0; i<words.size(); i+=MAX_WORDS) {
//                                    while(playinSound){
//                                        sleep(1000);
//                                    }
//                                    String tempText = "";
//                                    for (int h = i; h < i + MAX_WORDS; h++) {
//                                        tempText += words.get(h) + " ";
//                                    }
//                                    createSoundFile(tempText.trim(), voice);
//                                    playSoundFile(tempText.trim()+voice);
//                                }
//                            }else{
//                                createSoundFile(transcription, voice);
//                                playSoundFile(transcription+voice);
//                            }
                            String fileName = transcription.substring(0, Math.min(transcription.length(), 30)).replace(".", "").replace(",", "").replace(" ", "").trim() + voice+".wav";
                            createSoundFile(transcription, voice,fileName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                //}

           }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //editText.setText(data.get(0));
                data.forEach(w -> {
                    Log.println(Log.INFO,"Text",w);
                    System.out.println(w);
                });
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
        speech.startListening(speechRecognizerIntent);

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
        FileOutputStream fos = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);

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

    private void initializeMediaPlayer() {
        player = new MediaPlayer();

        player.setOnBufferingUpdateListener((mp, percent) -> Log.i("Buffering", "" + percent));
    }

    public void startPlaying(String fileName) {
        try {
            player.reset();
            File file = new File(getContext().getFilesDir(), fileName);
            Uri fileUri = Uri.parse(file.getPath());
            player.setDataSource(getContext(), fileUri);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(mp -> player.start());
            player.setOnCompletionListener(mp -> speech.startListening(speechRecognizerIntent));
            player.prepareAsync();
        } catch (IllegalArgumentException | SecurityException
                | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

    }
//    public void playSoundFile(String fileName) throws IOException {
//        playinSound = true;
//        Log.println(Log.INFO,"I","Speech recognition locked");
//        File file = new File(getContext().getFilesDir(), fileName);
//        Uri fileUri = Uri.parse(file.getPath());
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setDataSource(getContext(), fileUri);
//
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            // start playing sound when it's ready
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//            }
//        });
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            // when the audio has finished then unlock the speech recognition
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                speech.startListening(speechRecognizerIntent);
//                Log.println(Log.INFO,"I","Speech recognition free");
//            }
//
//        });
//        mediaPlayer.prepareAsync();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //speech.stopListening();
        speech.destroy();
        if(player!=null) player.release();
        if(thread!=null) thread.interrupt();
    }
}
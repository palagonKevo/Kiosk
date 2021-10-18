package com.example.kiosk;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kiosk.databinding.FragmentSecondBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private SpeechRecognizer speech;
    private EditText editText;
    private EditText rasaText;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        editText = view.findViewById(R.id.text);
        rasaText = view.findViewById(R.id.rasa);
        editText.setText("");
        speech = SpeechRecognizer.createSpeechRecognizer(getContext());

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");

        speech.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                //editText.setText("");
                //editText.setHint("Reading for Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {
                editText.setText("");
                editText.setHint("Listening...");
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
                if(error==7){
                    speech.startListening(speechRecognizerIntent);
                } else {
                    System.out.println(error);
                }
            }

            @Override
            public void onResults(Bundle bundle) {
                //micButton.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String input = data.get(0);
                System.out.println(input.toLowerCase(Locale.ROOT));
                if(input.toLowerCase(Locale.ROOT).contains("hola")){
                    editText.setText(input.toLowerCase(Locale.ROOT));
                    String[] strSplit = input.toLowerCase(Locale.ROOT).split("hola",2);
                    System.out.println("USER INPUT: "+strSplit[1]);
                    AsyncTask<String, Void, JSONArray> response = new RetrieveFeedTask().execute(strSplit[1]);
                    String text = "";
                    try {
                        System.out.println(response.get());
                        JSONArray jsonArray = response.get();
                        for (int i = 0, size = jsonArray.length();i < size ; i++){
                            JSONObject item = jsonArray.getJSONObject(i);
                            text+=item.getString("text");
                        }

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("TEXT: "+text);
                    rasaText.setText(text);
                }
                speech.startListening(speechRecognizerIntent);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //editText.setText(data.get(0));
                data.forEach(w -> {
                    Log.println(Log.ASSERT,"Text",w);
                    System.out.println(w);
                });
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        speech.startListening(speechRecognizerIntent);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //speech.stopListening();
        speech.destroy();
    }
}
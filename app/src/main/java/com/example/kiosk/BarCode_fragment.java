package com.example.kiosk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kiosk.databinding.FragmentBarCodeBinding;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class BarCode_fragment extends Fragment {

    private FragmentBarCodeBinding binding;
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private TextView barcodeText;
    private String barcodeData;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentBarCodeBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.previous.setOnClickListener(v -> NavHostFragment.findNavController(BarCode_fragment.this)
                .navigate(R.id.action_barCode_fragment_to_FirstFragment));
        surfaceView = view.findViewById(R.id.surface_view);
        barcodeText = view.findViewById(R.id.barcode_text);
        initialiseDetectorsAndSources();
    }
    @Override
    public void onPause() {
        super.onPause();
        cameraSource.release();
    }
    @Override
    public void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(() -> {

                        if (barcodes.valueAt(0).email != null) {
                            barcodeText.removeCallbacks(null);
                            barcodeData = barcodes.valueAt(0).email.address;
                            barcodeText.setText(barcodeData);
                            AsyncTask<String, Void, String> response = new RetrieveNutritionalInfo().execute(barcodeData);
                            try {
                                System.out.println(response.get());
                                Intent intent = new Intent(getActivity(), NutritionalInfo.class);
                                intent.putExtra("productData", response.get());
                                startActivity(intent);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {

                            barcodeData = barcodes.valueAt(0).displayValue;
                            barcodeText.setText(barcodeData);
                            AsyncTask<String, Void, String> response = new RetrieveNutritionalInfo().execute(barcodeData);
                            try {
                                System.out.println(response.get());
                                Intent intent = new Intent(getActivity(), NutritionalInfo.class);
                                intent.putExtra("productData", response.get());
                                startActivity(intent);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }
            }
        });
    }
}
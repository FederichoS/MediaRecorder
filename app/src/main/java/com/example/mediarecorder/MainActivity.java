package com.example.mediarecorder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private TextView textLight, textRec;
    private ImageButton btnLight, btnRec;
    private boolean btnRecState = true;

    private File folderPath;
    private String fileName = "/audio004.mp3";
    private String outputPath;
    FirebaseStorage storage;
    private static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLight = findViewById(R.id.textLight);
        textRec = findViewById(R.id.textRec);

        btnLight = findViewById(R.id.btnLight);
        btnRec = findViewById(R.id.btnRec);

      /* percorso locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            folderPath = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_RECORDINGS).toURI());
            outputPath = folderPath + fileName;
        }*/
        // su Firebase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            folderPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_RECORDINGS);
            outputPath = folderPath.getAbsolutePath() + fileName;
        }


        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* per percoso locale
                if (btnRecState) {
                    startRecording();
                    textRec.setText("Ferma registrazione");
                    btnRecState = false;
                } else {
                    stopRecording();
                    Log.e("test", fileName);
                    btnRecState = true;
                    textRec.setText("Avvia registrazione");
                }*/
                toggleRecording();
            }
        });

        requestPermissions();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_CODE);
        } else {
            Log.d("PERMISSIONS", "Permissions granted");
            Toast.makeText(this, "Permissions OK", Toast.LENGTH_SHORT).show();
        }

    }

    private void toggleRecording(){
        if (btnRecState){
            startRecording();
            textRec.setText("Ferma registrazione");
        }else {
            stopRecording();
            textRec.setText("Avvia registrazione");
        }
        btnRecState = !btnRecState;
    }

    private void startRecording() {
        Toast.makeText(this, "Dentro Start recording", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Path: " + outputPath, Toast.LENGTH_SHORT).show();
        Log.i("PATH", outputPath);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputPath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            // inizializza firebasestorage solo una volta
            if (storage == null){
                storage = FirebaseStorage.getInstance();
             }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR MEDIARECORDER", e.getMessage());
            Toast.makeText(this, "Error starting recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        Toast.makeText(this, "Dentro stopRecording", Toast.LENGTH_SHORT).show();
        Log.e("MEDIARECORDER", "Stopping recording");

        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();

                uploadAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR MEDIARECORDER", e.getMessage());
        } finally {
            mediaRecorder = null;
        }
    }

    private void uploadAudio() {
        if (storage != null) {
            StorageReference storageRef = storage.getReference();
            Uri audioFileUri = Uri.fromFile(new File(outputPath));

            // Creare una referenza al percorso su Firebase Storage
            StorageReference audioRef = storageRef.child("audio/" + audioFileUri.getLastPathSegment());

            // Eseguire l'upload del file
            audioRef.putFile(audioFileUri)
                    .addOnSuccessListener(taskSnapshot ->
                            Toast.makeText(MainActivity.this, "Upload success", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btnRecState) {
            stopRecording();
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("PERMISSIONS", "Permission granted after request");
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("PERMISSIONS", "Permission not granted after request");
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
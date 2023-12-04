package com.example.mediarecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mediarecorder.databinding.ActivityKotlinBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
class KotlinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKotlinBinding
    private var mediaRecorder: MediaRecorder? = null
    private var btnRecState = true
    private var folderPath: File? = null
    private val fileName = "/audio004.mp3"
    private lateinit var outputPath: String
    @JvmField
    var storage: FirebaseStorage? = null
    private val REQUEST_PERMISSION_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKotlinBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RECORDINGS)
            outputPath = "${folderPath.absolutePath}$fileName"
        }

        binding.btnRec.setOnClickListener {
            toggleRecording()
        }

        requestPermissions()

    }

    private fun requestPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED){
            
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_PERMISSION_CODE
            )
        }else{
            Log.d("permissions", "permesso concesso")
            Toast.makeText(this, "permesso concesso", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun toggleRecording(){
        if (btnRecState){
            startRecoding()
            binding.textRec.text = "Ferma registrazione"
        }else{
            stopRecording()
            binding.textRec.text = "Avvia registrazione"
        }
    }
    
    private fun startRecoding(){
        Toast.makeText(this, "dentro startRecording", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "path: $outputPath", Toast.LENGTH_SHORT).show()
        Log.i("PATH", outputPath)
        try {
            mediaRecorder = MediaRecorder()
            //il simbolo ? serve per dire che se c'è il dato esegue la riga, altrimenti non c'è
            //se metto il simbolo ! deve eseguirlo per forza, quindi se non c'è il dato l'app può andare in crash
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.setOutputFile(outputPath)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            if (storage == null){
                storage = FirebaseStorage.getInstance()
            }
        }catch (e: IOException){
            e.printStackTrace()
            Log.e("ERROR MEDIARECORDER", e.message!!)
            Toast.makeText(this, "error starting recording", Toast.LENGTH_SHORT).show()
        }
    }
}
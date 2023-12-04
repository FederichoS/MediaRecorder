package com.example.mediarecorder

import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.mediarecorder.databinding.ActivityKotlinBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class KotlinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKotlinBinding
    private var mediaRecorder: MediaRecorder? = null
    private var btnRecState = true
    private var folderPath: File? = null
    private val fileName = "/audio004.mp3"
    private var outputPath: String? = null
    @JvmField
    var storage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKotlinBinding.inflate(layoutInflater);

        setContentView(binding.root)


    }
}
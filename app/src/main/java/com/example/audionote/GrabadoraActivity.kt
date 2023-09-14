package com.example.audionote

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class GrabadoraActivity : AppCompatActivity() {
    lateinit var recorder: MediaRecorder
    lateinit var player: MediaPlayer
    lateinit var archivo: File
    var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grabadora)

        val btnGrabar = findViewById<Button>(R.id.btn_iniciar_grabacion)
        val tv1 = findViewById<TextView>(R.id.tv_grabando)
        val btnDetener = findViewById<Button>(R.id.btn_detener_grabacion)
        val btnPausar = findViewById<Button>(R.id.btn_pausar_grabacion)

        // Comprueba y solicita permisos de grabación si es necesario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }

        // Inicializa MediaRecorder
        recorder = MediaRecorder()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        archivo = File(getExternalFilesDir(null), "/grabacion_$timeStamp.3gp")

        print("444444444444444444444444444444444444444444444444")
        print(archivo.absolutePath)
        player = MediaPlayer()
        btnGrabar.setOnClickListener {
            if (!isRecording) {
                try {
                    recorder.reset()
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    recorder.setOutputFile(archivo.absolutePath)
                    recorder.prepare()
                    recorder.start()
                    isRecording = true

                    btnGrabar.visibility = View.INVISIBLE
                    btnDetener.visibility = View.VISIBLE
                    btnPausar.isEnabled = true
                    tv1.text = "Grabando..."
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        btnDetener.setOnClickListener {
            if (isRecording) {
                recorder.stop()
                recorder.release()
                isRecording = false
            }

            if (player.isPlaying) {
                player.stop()
                player.release()
            }

            player = MediaPlayer()
            try {
                player.setDataSource(archivo.absolutePath)
                player.prepare()
                player.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            btnGrabar.visibility = View.VISIBLE
            btnDetener.visibility = View.INVISIBLE
            btnPausar.isEnabled = true
            tv1.text = "Listo"
        }

        btnPausar.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                btnPausar.isEnabled = false
            }
        }
    }
}
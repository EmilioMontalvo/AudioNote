package com.example.audionote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible

class GrabadoraActivity : AppCompatActivity() {
    lateinit var recorder: MediaRecorder
    lateinit var player: MediaPlayer
    var isRecording = false
    var isPaused = false
    var direcionDeArchivo=""
    val callback=  registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            // Verifica si el usuario otorgó los permisos necesarios
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Los permisos fueron otorgados, puedes realizar la acción que los requiere.
            } else {
                // Los permisos fueron denegados, muestra un mensaje al usuario o realiza una acción alternativa.
                // Por ejemplo, puedes mostrar un Toast indicando que los permisos son necesarios.
                Toast.makeText(this, "Los permisos son necesarios para grabar y acceder al almacenamiento.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grabadora)

        val btnGrabar = findViewById<Button>(R.id.btn_iniciar_grabacion)
        val tv1 = findViewById<TextView>(R.id.tv_grabando)
        val btnDetener = findViewById<Button>(R.id.btn_detener_grabacion)
        val btnPausar = findViewById<Button>(R.id.btn_pausar_grabacion)
        val btnContinuar = findViewById<Button>(R.id.btn_despausar)
        val btnGuardar=findViewById<Button>(R.id.btn_guardar)

        player = MediaPlayer()
        btnPausar.isEnabled = false
        btnGuardar.isEnabled = false

        // Comprueba y solicita permisos de grabación si es necesario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                1
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, 2)
            }
        }


        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

        val archivo = File(directory, "grabacion_$timeStamp.3gp")
        direcionDeArchivo=archivo.absolutePath;



        btnGrabar.setOnClickListener {

            if (!isRecording) {
                try {
                    recorder = MediaRecorder()
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
                    btnPausar.visibility = View.VISIBLE
                    btnPausar.isEnabled = true
                    tv1.text = "Grabando..."
                    btnGuardar.isEnabled = false

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
                println(archivo.absolutePath)
                player.prepare()
                player.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            btnGrabar.visibility = View.VISIBLE
            btnDetener.visibility = View.INVISIBLE
            btnPausar.isEnabled = false
            btnContinuar.isVisible = false
            btnGuardar.isEnabled = true
            tv1.text = "Listo"
        }

        btnPausar.setOnClickListener {
            if (isRecording) {
                recorder.pause()
                btnPausar.isVisible = false
                btnContinuar.isVisible = true
                tv1.text = "Grabacion Pausada"
                isPaused=true
            }

        }

        btnContinuar.setOnClickListener{
            if(isPaused){
                recorder.resume()
                btnPausar.isVisible = true
                btnContinuar.isVisible = false
                tv1.text = "Grabando..."
                isPaused=false
            }
        }

        btnGuardar.setOnClickListener{
            abrirActividadConParametros(FormularioGrabacionActivity::class.java)
        }
    }

    fun abrirActividadConParametros(
        clase: Class<*>
    ){
        val intentExplicito = Intent(this, clase)
        intentExplicito.putExtra("path", direcionDeArchivo)
        callback.launch(intentExplicito)
    }


}
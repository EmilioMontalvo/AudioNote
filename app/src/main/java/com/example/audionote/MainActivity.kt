package com.example.audionote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.audionote.modelos.Usuario
import com.example.audionote.vistas.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val usuario= Usuario("1","manuel","manuel@epn.edu.ec");
        val botonInicioSesion=findViewById<Button>(R.id.btn_login_main)
        botonInicioSesion.setOnClickListener {
                irActividad(LoginActivity::class.java)
        }
    }

    fun irActividad(
        clase: Class<*>
    ){
        val intent = Intent(this, clase)
        startActivity(intent)
    }
}
package com.example.audionote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.audionote.modelos.NotaDeVoz
import com.google.firebase.auth.FirebaseAuth


class PrincipalActivity : AppCompatActivity() {

    val arreglo: ArrayList<NotaDeVoz> = arrayListOf()
    var uid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid!=null){
            val titulo=findViewById<TextView>(R.id.tv_titulo)
            titulo.text=uid

            val botonLogout=findViewById<Button>(R.id.btn_logout_principal)
            botonLogout.setOnClickListener{
                abrirDialogoLogOut()
            }

            val botonGrabar=findViewById<Button>(R.id.btn_grabacion_principal)
            botonGrabar.setOnClickListener{
                irActividad(GrabadoraActivity::class.java)
            }
        }
    }

    fun abrirDialogoLogOut() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Esta seguro que desea salir?")
        builder.setPositiveButton("Si") { dialog, which ->
            seDeslogeo()
        }
        builder.setNegativeButton("No", null)

        val dialog = builder.create()
        dialog.show()
    }

    fun seDeslogeo(){
       FirebaseAuth.getInstance().signOut()
       irActividad(MainActivity::class.java)
    }

    fun irActividad(
        clase: Class<*>
    ){
        val intent = Intent(this, clase)
        startActivity(intent)
    }
}
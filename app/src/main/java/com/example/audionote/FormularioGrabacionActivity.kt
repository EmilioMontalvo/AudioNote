package com.example.audionote

import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.audionote.R
import com.example.audionote.modelos.NotaDeVoz
import com.example.audionote.modelos.Notificacion
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class FormularioGrabacionActivity : AppCompatActivity() {
    var rutaArchivo:String?=null
    var idNota:String?=null
    var idNotificacion:String?=null
    lateinit var player: MediaPlayer

    lateinit var  viewTitulo:EditText
    lateinit var  viewDespcion:TextInputEditText
    lateinit var  viewFecha:EditText
    lateinit var  viewHora:EditText
    lateinit var  viewH1:TextView

    var uid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_grabacion)
        rutaArchivo = intent.getStringExtra("path")
        idNota = intent.getStringExtra("idNota")
        uid = FirebaseAuth.getInstance().currentUser?.uid
        player = MediaPlayer()


        if (uid!=null){


            viewTitulo = findViewById<EditText>(R.id.et_titulo)
            viewDespcion = findViewById<TextInputEditText>(R.id.ti_descripcion)
            viewFecha=findViewById<EditText>(R.id.etd_fecha)
            viewHora=findViewById<EditText>(R.id.etd_hora)
            viewH1=findViewById<TextView>(R.id.tv_grabacion_lista)
            val botonGuardar=findViewById<Button>(R.id.btn_guardar_formulario)
            val botonDescartar=findViewById<Button>(R.id.btn_descartar)
            val botonReproducir=findViewById<ImageButton>(R.id.btn_play_formulario)

            botonDescartar.setOnClickListener{
                irActividad(PrincipalActivity::class.java)
            }


            if(rutaArchivo!=null){

                botonGuardar.setOnClickListener{
                    try {
                        val titulo=viewTitulo.text.toString()
                        val descripcion=viewDespcion.text.toString()
                        val fecha=viewFecha.text.toString()
                        val hora=viewHora.text.toString()

                        if(
                            titulo!="" && descripcion!=""

                        ){
                            val nota1= NotaDeVoz(
                                "",
                                titulo,
                                descripcion,
                                rutaArchivo!!,
                                Timestamp.now() ,
                                Timestamp.now(),
                                uid!!)


                            if(fecha!="" && hora!=""){
                                val fechaString = fecha
                                val horaString = hora


                                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                                try {
                                    // Parsea la fecha y hora a un objeto Date
                                    val fechaHora = dateFormat.parse("$fechaString $horaString")

                                    // Crea un objeto Timestamp a partir de la fecha y hora
                                    val timestamp = if (fechaHora != null) {
                                        Timestamp(fechaHora)
                                    } else {
                                        null
                                    }

                                    if (timestamp != null) {
                                        val notificacion=Notificacion("",nota1.id,timestamp)
                                        abrirDialogo("Nota guardada con exito")
                                        nota1.add()
                                        notificacion.add()
                                        irActividad(PrincipalActivity::class.java)
                                    } else {
                                        abrirDialogo("mmmmmmmmmmmm")
                                    }
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }


                            }else{
                                if(fecha!=null || hora!=null){
                                    abrirDialogo("Faltan datos por para la notificacion")
                                }else{
                                    abrirDialogo("Nota guardada con exito")
                                    nota1.add()
                                    irActividad(PrincipalActivity::class.java)
                                }

                            }
                        }else{
                            abrirDialogo("Faltan datos por ingresar")
                        }

                    }catch (ex: Exception){
                        print(ex.stackTrace)
                    }
                }
                botonReproducir.setOnClickListener{
                    playAudio(rutaArchivo!!)
                }

            }else if(idNota!=null){

                viewH1.text="Grabación"
                val notaDeVoz:NotaDeVoz=BDD.arreglo.first {it.id==idNota}
                viewTitulo.setText(notaDeVoz.titulo)
                viewDespcion.setText(notaDeVoz.descripcion)
                botonDescartar.text="Cancelar"


                val colectionReference = Firebase.firestore.collection("notificacion")

                colectionReference.whereEqualTo("idNotaDeVoz",idNota).get().addOnSuccessListener {documents ->

                    try {
                        idNotificacion=documents.first().id
                        val data = documents.first().data
                        val notificacion=Notificacion(
                            idNotificacion!!,
                            data?.get("idNotaDeVoz") as String,
                            data?.get("fechaNotificacion") as Timestamp
                        )
                        viewFecha.setText(notificacion.getFechaString())
                        viewHora.setText(notificacion.getHora())
                    }catch (ex: Exception){
                        idNotificacion=null
                        print(ex.stackTrace)
                    }

                }.addOnFailureListener{ }

                botonReproducir.setOnClickListener{
                    playAudio(notaDeVoz.rutaArchivo)
                }


                botonGuardar.setOnClickListener{
                    try {
                        val titulo=viewTitulo.text.toString()
                        val descripcion=viewDespcion.text.toString()
                        val fecha=viewFecha.text.toString()
                        val hora=viewHora.text.toString()

                        if(
                            titulo!="" && descripcion!=""

                        ){
                            val nota1= NotaDeVoz(
                                notaDeVoz.id,
                                titulo,
                                descripcion,
                                notaDeVoz.rutaArchivo,
                                notaDeVoz.fechaCreacion ,
                                Timestamp.now(),
                                uid!!)


                            if(fecha!="" && hora!=""){
                                val fechaString = fecha
                                val horaString = hora


                                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                                try {
                                    // Parsea la fecha y hora a un objeto Date
                                    val fechaHora = dateFormat.parse("$fechaString $horaString")

                                    // Crea un objeto Timestamp a partir de la fecha y hora
                                    val timestamp = if (fechaHora != null) {
                                        Timestamp(fechaHora)
                                    } else {
                                        null
                                    }

                                    if (timestamp != null) {

                                        if(idNotificacion==null){
                                            val notificacion=Notificacion("",nota1.id,timestamp)
                                            abrirDialogo("Nota actualizada con exito")
                                            nota1.edit()
                                            notificacion.add()
                                            irActividad(PrincipalActivity::class.java)
                                        }else{
                                            val notificacion=Notificacion(idNotificacion!!,nota1.id,timestamp)
                                            abrirDialogo("Nota actualizada con exito")
                                            nota1.edit()
                                            notificacion.edit()
                                            irActividad(PrincipalActivity::class.java)
                                        }

                                    } else {
                                        abrirDialogo("mmmmmmmmmmmm")
                                    }
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }


                            }else{
                                if(fecha!=null || hora!=null){
                                    abrirDialogo("Faltan datos por para la notificacion")
                                }else{
                                    abrirDialogo("Nota actualizada con exito")
                                    nota1.edit()
                                    irActividad(PrincipalActivity::class.java)
                                }

                            }
                        }else{
                            abrirDialogo("Faltan datos por ingresar")
                        }

                    }catch (ex: Exception){
                        print(ex.stackTrace)
                    }
                }


            }


        }

    }


    fun abrirDialogo(cadena:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(cadena)
        builder.setPositiveButton(
            "Aceptar",
            DialogInterface.OnClickListener{ // Callback
                    dialog, which -> dialog.cancel()
            }
        )

        val dialogo = builder.create()
        dialogo.setCancelable(false)
        dialogo.show()

    }

    fun irActividad(
        clase: Class<*>
    ){
        val intent = Intent(this, clase)
        // NO RECIBIMOS RESPUESTA
        startActivity(intent)
        // this.startActivity()
    }


    fun playAudio(path:String){
        if (player.isPlaying) {
            player.stop()
            player.release()
        }

        player = MediaPlayer()
        try {
            player.setDataSource(path)
            player.prepare()
            player.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
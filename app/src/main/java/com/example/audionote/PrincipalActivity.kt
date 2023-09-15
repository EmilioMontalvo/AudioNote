package com.example.audionote

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.audionote.modelos.NotaDeVoz
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.time.format.DateTimeFormatter


class PrincipalActivity : AppCompatActivity() {
    var query: Query? = null
    val arreglo: ArrayList<NotaDeVoz> = BDD.arreglo
    var uid:String?=null
    lateinit var player: MediaPlayer
    lateinit var adaptador:FRecyclerViewAdaptadorNotaDeVoz
    val callback=  registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        player = MediaPlayer()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid!=null){
            //crearDatosPrueba()
            val botonLogout=findViewById<ImageButton>(R.id.btn_logout_principal)
            botonLogout.setOnClickListener{
                abrirDialogoLogOut()
            }

            val botonGrabar=findViewById<Button>(R.id.btn_grabacion_principal)
            botonGrabar.setOnClickListener{
                irActividad(GrabadoraActivity::class.java)
            }

            val recyclerView = findViewById<RecyclerView>(R.id.rv_notas_voz)
            adaptador = FRecyclerViewAdaptadorNotaDeVoz(
                this,
                arreglo,
                recyclerView
            )
            recyclerView.adapter=adaptador
            recyclerView.itemAnimator=androidx.recyclerview.widget
                .DefaultItemAnimator()
            recyclerView.layoutManager=androidx.recyclerview.widget
                .LinearLayoutManager(this)
            adaptador.notifyDataSetChanged()


            consultarNotasDeVoz(adaptador)

        }
    }

    fun crearDatosPrueba(){

        val nota1=NotaDeVoz("","prueba","cumpleaños","content://com.android", now() ,now(),uid!!)
        nota1.add()

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

    fun consultarNotasDeVoz(
        adaptador: FRecyclerViewAdaptadorNotaDeVoz
    ){
        val db = Firebase.firestore
        val notasRef = db.collection("nota_de_voz")
            .whereEqualTo("uid", uid)
            .orderBy("fechaActualizacion", Query.Direction.DESCENDING)
            .limit(5)
        var tarea: Task<QuerySnapshot>? = null
        if(query== null){
            limpiarArreglo()
            adaptador.notifyDataSetChanged()
            tarea = notasRef.get()
        }else{
            tarea = query!!.get()
        }
        if(tarea != null){
            tarea
                .addOnSuccessListener {
                        documentSnapshots ->
                    guardarQuery(documentSnapshots, notasRef)

                    print(documentSnapshots.size())
                    for (nota in documentSnapshots){
                        anadirAArreglo(nota)
                    }
                    adaptador.notifyDataSetChanged()
                }
                .addOnFailureListener {  }
        }
    }
    fun limpiarArreglo() {arreglo.clear()}
    fun guardarQuery(
        documentSnapshot: QuerySnapshot,
        refNotasDeVoz: Query
    ){
        if(documentSnapshot.size() > 0){
            val ultimoDocumento = documentSnapshot
                .documents[documentSnapshot.size()-1]
            query = refNotasDeVoz
                .startAfter(ultimoDocumento)
        }
    }

    fun anadirAArreglo(
        documento: QueryDocumentSnapshot
    ){
        val data = documento.data
        // Definir el formato de la cadena
        val formato = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        val nuevaNota = NotaDeVoz(
            documento.id,
            data.get("titulo") as String,
            data.get("descripcion") as String,
            data.get("rutaArchivo") as String,
            data.get("fechaCreacion") as Timestamp,
            data.get("fechaActualizacion") as Timestamp,
            uid!!
        )


        arreglo.add(nuevaNota)
    }


    fun eliminar(notaDeVoz: NotaDeVoz){
        notaDeVoz.delete()
        arreglo.removeIf{it.id==notaDeVoz.id}
        adaptador.notifyDataSetChanged()
    }

    fun abrirActividadConParametros(
        clase: Class<*>,
        nota:NotaDeVoz
    ){
        val intentExplicito = Intent(this, clase)
        intentExplicito.putExtra("idNota", nota.id)
        callback.launch(intentExplicito)
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
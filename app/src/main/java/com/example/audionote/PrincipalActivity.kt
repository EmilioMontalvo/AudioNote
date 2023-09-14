package com.example.audionote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
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
import java.time.format.DateTimeFormatter


class PrincipalActivity : AppCompatActivity() {
    var query: Query? = null
    val arreglo: ArrayList<NotaDeVoz> = arrayListOf()
    var uid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)


        uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid!=null){
            //crearDatosPrueba()
            val botonLogout=findViewById<Button>(R.id.btn_logout_principal)
            botonLogout.setOnClickListener{
                abrirDialogoLogOut()
            }

            val botonGrabar=findViewById<Button>(R.id.btn_grabacion_principal)
            botonGrabar.setOnClickListener{
                irActividad(GrabadoraActivity::class.java)
            }

            val listView = findViewById<ListView>(R.id.lv_notas_voz)
            val adaptador = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                arreglo
            )
            listView.adapter = adaptador
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
        adaptador: ArrayAdapter<NotaDeVoz>
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
}
package com.example.audionote.modelos


import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class NotaDeVoz(
    var id:String = "",
    var titulo:String = "",
    var descripcion:String = "",
    var rutaArchivo:String = "",
    var fechaCreacion: LocalDate? =null,
    var fechaActualizacion: LocalDate? =null
    ) {
    private val colectionReference: CollectionReference = Firebase.firestore.collection("nota_de_voz")
    fun add(){
        this.id=System.currentTimeMillis().toString()
        val data = hashMapOf(
            "titulo" to titulo,
            "descripcion" to descripcion,
            "rutaArchivo" to rutaArchivo,
            "fechaCreacion" to fechaCreacion.toString(),
            "fechaActualizacion" to fechaActualizacion.toString()
        )

        colectionReference.document(id)
            .set(data)
            .addOnSuccessListener {  }
            .addOnFailureListener {  }
    }

    fun delete(){
        colectionReference.document(id)
            .delete()
            .addOnFailureListener {  }
            .addOnFailureListener {  }
    }

    fun edit(){
        val data = hashMapOf(
            "titulo" to titulo,
            "descripcion" to descripcion,
            "rutaArchivo" to rutaArchivo,
            "fechaCreacion" to fechaCreacion.toString(),
            "fechaActualizacion" to fechaActualizacion.toString()
        )

        colectionReference.document(id)
            .set(data)
            .addOnSuccessListener {  }
            .addOnFailureListener {  }
    }
}
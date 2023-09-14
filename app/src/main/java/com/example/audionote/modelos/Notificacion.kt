package com.example.audionote.modelos

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime

class Notificacion(
    var id:String="",
    var idNotaDeVoz:String="",
    var fechaNotificacion: LocalDateTime? =null

) {
    private val colectionReference: CollectionReference = Firebase.firestore.collection("notificacion")
    fun add(){
        this.id=System.currentTimeMillis().toString()
        val data = hashMapOf(
            "idNotaDeVoz" to idNotaDeVoz,
            "fechaNotificacion" to fechaNotificacion.toString()
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
            "idNotaDeVoz" to idNotaDeVoz,
            "fechaNotificacion" to fechaNotificacion.toString()
        )

        colectionReference.document(id)
            .set(data)
            .addOnSuccessListener {  }
            .addOnFailureListener {  }
    }
}
package com.example.audionote.modelos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

class Notificacion(
    var id:String="",
    var idNotaDeVoz:String="",
    var fechaNotificacion: Timestamp,


) {
    private val colectionReference: CollectionReference = Firebase.firestore.collection("notificacion")
    fun add(){
        this.id=System.currentTimeMillis().toString()
        val data = hashMapOf(
            "idNotaDeVoz" to idNotaDeVoz,
            "fechaNotificacion" to fechaNotificacion,

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
            "fechaNotificacion" to fechaNotificacion
        )

        colectionReference.document(id)
            .set(data)
            .addOnSuccessListener {  }
            .addOnFailureListener {  }
    }

    fun getHora(): String {
        val date = fechaNotificacion.toDate()
        val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatoHora.format(date)
    }

    // Método para extraer la fecha en formato dd/MM/yyyy
    fun getFechaString(): String {
        val date = fechaNotificacion.toDate()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatoFecha.format(date)
    }
}
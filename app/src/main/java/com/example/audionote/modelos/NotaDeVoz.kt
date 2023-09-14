package com.example.audionote.modelos


import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotaDeVoz(
    var id: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var rutaArchivo: String = "",
    var fechaCreacion: Timestamp,
    var fechaActualizacion: Timestamp,
    var uid: String = ""
) {
    private val collectionReference: CollectionReference = FirebaseFirestore.getInstance().collection("nota_de_voz")

    fun add() {
        this.id=System.currentTimeMillis().toString()
        val data = hashMapOf(
            "titulo" to titulo,
            "descripcion" to descripcion,
            "rutaArchivo" to rutaArchivo,
            "fechaCreacion" to fechaCreacion,
            "fechaActualizacion" to fechaActualizacion,
            "uid" to uid
        )

        collectionReference.document(id)
            .set(data)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    fun delete() {
        collectionReference.document(id)
            .delete()
            .addOnFailureListener { }
    }

    fun edit() {
        val data = hashMapOf(
            "titulo" to titulo,
            "descripcion" to descripcion,
            "rutaArchivo" to rutaArchivo,
            "fechaCreacion" to fechaCreacion,
            "fechaActualizacion" to fechaActualizacion,
            "uid" to uid
        )

        collectionReference.document(id)
            .set(data)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }


    fun getFechaActualizacionString():String {
        val date = fechaActualizacion?.toDate()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        return formattedDate.toString();
    }

    override fun toString(): String {
        val date = fechaActualizacion?.toDate()


        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        return "titulo: $titulo uid: ${formattedDate.toString()}"
    }
}
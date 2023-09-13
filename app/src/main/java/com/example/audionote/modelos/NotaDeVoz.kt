package com.example.audionote.modelos

import java.time.LocalDate

class NotaDeVoz(
    var id:String = "",
    var titulo:String = "",
    var descripcion:String = "",
    var rutaArchivo:String = "",
    var fechaCreacion: LocalDate? =null,
    var fechaActualizacion: LocalDate? =null,

    ) {
}
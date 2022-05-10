package com.example.purrrfectpoi.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class TrabajosModel {
    var id : String = ""
    var Autor : DocumentReference? = null
    var Documento : String = ""
    var TituloDocumento : String = ""
    var FechaEntregada : Timestamp? = null
    var TareaRevisada : Boolean? = null
}
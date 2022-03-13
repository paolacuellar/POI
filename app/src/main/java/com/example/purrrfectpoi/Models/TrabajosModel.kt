package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class TrabajosModel {
    var Autor : DocumentReference? = null
    var Documento : String = ""
    var FechaEntregada : Date? = null
}
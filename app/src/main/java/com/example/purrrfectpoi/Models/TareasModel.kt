package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class TareasModel {
    var id : String = ""
    var Nombre : String = ""
    var Descripcion : String = ""
    var FechaCreacion : Date? = null
    var FechaEntregaProgramada : Date? = null
    var TrabajosAlumnos : ArrayList<DocumentReference> = arrayListOf()
}
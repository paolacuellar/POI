package com.example.purrrfectpoi.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class TareasModel {
    var id : String = ""
    var Nombre : String = ""
    var Descripcion : String = ""
    var FechaCreacion : Timestamp? = null
    var FechaProgramada : Timestamp? = null
    var TrabajosAlumnos : ArrayList<DocumentReference> = arrayListOf()
}
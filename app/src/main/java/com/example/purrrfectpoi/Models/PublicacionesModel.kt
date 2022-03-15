package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class PublicacionesModel {
    var Texto : String = ""
    var Foto : String = ""

    var Creador : DocumentReference? = null
    var FechaCreacion : Date? = null
    var Editado : Boolean = false
    var Latitud : String = ""
    var Longitud : String = ""

    var Comentarios : ArrayList<DocumentReference> = arrayListOf()
}
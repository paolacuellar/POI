package com.example.purrrfectpoi.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class ComentariosModel {
    var id : String = ""
    var Texto : String = ""

    var Creador : DocumentReference? = null
    var FechaCreacion : Timestamp? = null
    var Editado : Boolean = false
}
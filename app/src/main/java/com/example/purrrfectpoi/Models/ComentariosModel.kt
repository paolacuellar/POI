package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class ComentariosModel {
    var id : String = ""
    var Texto : String = ""

    var Creador : DocumentReference? = null
    var FechaCreacion : Date? = null
    var Editado : Boolean = false
}
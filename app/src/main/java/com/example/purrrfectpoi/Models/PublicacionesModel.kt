package com.example.purrrfectpoi.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class PublicacionesModel {
    var id : String = ""
    var Texto : String = ""
    var Foto : String = ""

    var Creador : DocumentReference? = null
    var FechaCreacion : Timestamp? = null
    var Editado : Boolean = false
    var Latitud : String = ""
    var Longitud : String = ""

    var Comentarios : ArrayList<DocumentReference> = arrayListOf()

    var NombreCreador : String = ""
    var FotoCreador : String = ""
}
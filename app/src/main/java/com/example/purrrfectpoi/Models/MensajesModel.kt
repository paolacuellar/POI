package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference
import com.google.type.Date

class MensajesModel {
    var id : String = ""
    var Texto : String = ""
    var Autor : DocumentReference? = null
    var Foto : String = ""
    var NombreDocumento : String = ""
    var Latitud : String = ""
    var Longitud : String = ""
    var FechaCreacion : Date? = null
}
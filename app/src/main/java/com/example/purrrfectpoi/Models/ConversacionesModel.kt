package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference

class ConversacionesModel {
    var id : String = ""
    var Participantes : DocumentReference? = null
    var Mensajes : ArrayList<DocumentReference> = arrayListOf()
}
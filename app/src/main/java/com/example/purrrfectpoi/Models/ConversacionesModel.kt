package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference

class ConversacionesModel {
    var id : String = ""
    var Participantes : ArrayList<DocumentReference> = arrayListOf()
    var Mensajes : ArrayList<DocumentReference> = arrayListOf()
}
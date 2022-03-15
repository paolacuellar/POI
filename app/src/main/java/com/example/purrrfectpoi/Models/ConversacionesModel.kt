package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference

class ConversacionesModel {
    var Participantes : DocumentReference? = null
    var Mensajes : ArrayList<DocumentReference> = arrayListOf()
}
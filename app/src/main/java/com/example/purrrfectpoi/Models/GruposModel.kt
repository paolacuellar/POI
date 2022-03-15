package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference

class GruposModel {
    var Nombre : String = ""
    var Descripción : String = ""
    var Foto : String = ""
    var Creador : DocumentReference? = null
    var Miembros : ArrayList<DocumentReference> = arrayListOf()
    var Conversacion : DocumentReference? = null
    var Tareas : ArrayList<DocumentReference> = arrayListOf()
}
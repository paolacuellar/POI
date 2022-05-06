package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference

class UsuariosModel {
    var Nombre : String = ""
    var ApPaterno : String = ""
    var ApMaterno : String = ""
    var Email : String = ""

    var Foto : String = ""
    var Carrera : DocumentReference? = null
    var Conversaciones : ArrayList<DocumentReference> = arrayListOf()

    var Conectado : Boolean = false

    var CantidadTareas : Long = 0
    var CantidadGrupos : Long = 0
    var CantidadPosts : Long = 0
}
package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference

class CarrerasModel {
    var Nombre : String = ""
    var Publicaciones : ArrayList<DocumentReference> = arrayListOf()
    var Grupos : ArrayList<DocumentReference> = arrayListOf()
}
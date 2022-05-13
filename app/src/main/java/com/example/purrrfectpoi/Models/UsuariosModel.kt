package com.example.purrrfectpoi.Models

import com.google.firebase.firestore.DocumentReference
import com.poi.kadala.utils.Encrypt

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

    var Ecriptado : Boolean = false

    fun EcriptarInfo(){
        if (Ecriptado){
            this.Nombre = Encrypt.encrypt(this.Nombre).toString()
            this.ApPaterno = Encrypt.encrypt(this.ApPaterno).toString()
            this.ApMaterno = Encrypt.encrypt(this.ApMaterno).toString()
        }
    }

    fun DesencriptarInfo(){
        if (Ecriptado){
            this.Nombre = Encrypt.decrypt(this.Nombre).toString()
            this.ApPaterno = Encrypt.decrypt(this.ApPaterno).toString()
            this.ApMaterno = Encrypt.decrypt(this.ApMaterno).toString()
        }
    }
}
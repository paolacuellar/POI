package com.psm.hiring.Utils

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import java.math.BigInteger

@SuppressLint("StaticFieldLeak")
object DataManager {
    //var IdUsuario:BigInteger? = null
    var emailUsuario:String? = null
    var fotoUsuario:String? = null

    var context: Context? = null

    var progressDialog:  ProgressDialog? = null

    var ImageAux: String? = null

    fun isStringEmpty(stringVar:String?): Boolean {
        if(stringVar != null){
            if (stringVar != "")
                return false
            else
                return true
        }
        else
            return true
    }


    fun showAlert (context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
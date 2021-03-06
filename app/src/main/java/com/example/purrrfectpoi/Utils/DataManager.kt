package com.psm.hiring.Utils

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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

    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showProgressDialog(message: String){
        this.progressDialog!!.setMessage(message)
        this.progressDialog!!.setCancelable(false)
        this.progressDialog!!.show()
    }

    fun updateMessage(message: String){
        this.progressDialog!!.setMessage(message)
    }

    fun hideProgressDialog(){
        if(this.progressDialog!!.isShowing) this.progressDialog!!.dismiss()
    }


    fun TimeStampToDayHourYear(timestamp: Timestamp?) : String{
        if (timestamp == null)
            return "dd/MM/yyyy"

        val dateConverted = Date(timestamp!!.seconds * 1000)

        val year = dateConverted.year + 1900
        val month = dateConverted.month
        val monthText = getMonthInText(month + 1)
        val day = dateConverted.date

        return "$day/$monthText/$year"
    }


    fun TimeStampToDayHourYear_Hour_Minute(timestamp: Timestamp?) : String{
        if (timestamp == null)
            return "dd/MM/yyyy hh:mm"

        val dateConverted = Date(timestamp!!.seconds * 1000)

        val year = dateConverted.year + 1900
        val month = dateConverted.month
        val monthText = getMonthInText(month + 1)
        val day = dateConverted.date
        val hour = dateConverted.hours
        val minute = dateConverted.minutes

        return "$day/$monthText/$year $hour:$minute"
    }

    private fun getMonthInText(month : Int) : String{
        return when(month){
            1 -> "Ene"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Abr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Ago"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dic"

            else -> "Error en mes"
        }
    }


    fun getTimeStamptToday() : Timestamp{
        var calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var dateLimit = "$year-${month + 1}-$day 00:00:00"
        val format = SimpleDateFormat("yyy-MM-dd HH:mm:ss")
        val date = format.parse(dateLimit)
        val dateTimestampLimit = Timestamp(date.time / 1000, 0)

        return dateTimestampLimit
    }

    fun updateBadges(opc: String) {
        FirebaseFirestore.getInstance().collection("Usuarios").document(this.emailUsuario!!)
            .get()
            .addOnSuccessListener { responseUsuario ->

                var cantGrupos = responseUsuario.get("CantidadGrupos") as Long
                var cantPosts = responseUsuario.get("CantidadPosts") as Long
                var cantTareas = responseUsuario.get("CantidadTareas") as Long

                when(opc) {
                    "Grupos" ->{
                        cantGrupos++
                        FirebaseFirestore.getInstance().collection("Usuarios").document(this.emailUsuario!!)
                            .update(
                                "CantidadGrupos", cantGrupos
                            )
                    }
                    "Posts" ->{
                        cantPosts++
                        FirebaseFirestore.getInstance().collection("Usuarios").document(this.emailUsuario!!)
                            .update(
                                "CantidadPosts", cantPosts
                            )
                    }
                    "Tareas" ->{
                        cantTareas++
                        FirebaseFirestore.getInstance().collection("Usuarios").document(this.emailUsuario!!)
                            .update(
                                "CantidadTareas", cantTareas
                            )
                    }
                }

            }
    }

    fun updateUserConnected(isConnected : Boolean){
        if (emailUsuario != null) {
            if (emailUsuario != "") {
                FirebaseFirestore.getInstance().collection("Usuarios")
                    .document(DataManager.emailUsuario!!)
                    .update(
                        mapOf(
                            "Conectado" to isConnected
                        )
                    ).addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Log.e(TAG, "Hubo un error al actualizar conexi??n del usuario" + it.exception?.message)
                        }
                    }
            }
        }

    }
}
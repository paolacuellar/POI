package com.example.purrrfectpoi

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.purrrfectpoi.fragments.ChatsFragment
import com.example.purrrfectpoi.fragments.GruposFragment
import com.example.purrrfectpoi.fragments.MuroFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.psm.hiring.Utils.DataManager
import java.math.BigInteger
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    val TAG="LoginActivity"


    companion object {
        var instance: LoginActivity? = null
    }

    init{
        instance = this
    }

    var btn_Login : Button? = null;
    var lbl_Register : TextView? = null;
    var editTextEmail : EditText? = null;
    var editTextPassword : EditText? = null;


    //var UsuarioLoggin : Usuarios_Model? = Usuarios_Model();


    var CorreoUsuario : String? = null
    var PasswordUsuario : String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        DataManager.progressDialog = ProgressDialog(this)

        this.btn_Login = findViewById<Button>(R.id.login_btn_login)
        this.lbl_Register = findViewById<TextView>(R.id.login_lbl_registrarse)
        this.editTextEmail = findViewById<EditText>(R.id.login_input_email)
        this.editTextPassword = findViewById<EditText>(R.id.login_input_pw)


        this.btn_Login?.setOnClickListener {
            logginUsuario(this)

        }

        this.lbl_Register?.setOnClickListener {
            val vIntent = Intent(this, RegisterActivity::class.java)
            startActivity(vIntent)
        }

        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = ""
        val IdUsuarioActivo = sharedPref.getString("IdUsuarioActivo", defaultValue)


        if(IdUsuarioActivo != ""){
            DataManager.IdUsuario = BigInteger(IdUsuarioActivo)

            val vIntent =  Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }
    }


    fun logginUsuario(loginActivity: LoginActivity) {

        DataManager.progressDialog!!.setMessage("Ingresando")
        DataManager.progressDialog!!.setCancelable(false)
        DataManager.progressDialog!!.show()


        CorreoUsuario = editTextEmail?.text.toString()
        PasswordUsuario = editTextPassword?.text.toString()

        if(CorreoUsuario!!.isNullOrEmpty() || PasswordUsuario!!.isNullOrEmpty()){

            if(editTextEmail?.text.isNullOrEmpty()){
                editTextEmail?.setError("Inserte un correo valido")
            }
            if(editTextPassword?.text.isNullOrEmpty()){
                editTextPassword?.setError("Campo vacio")
            }
            if(DataManager.progressDialog!!.isShowing) DataManager.progressDialog!!.dismiss()
        }else{
            val vIntent =  Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }

    }


    override fun onResume() {
        super.onResume()

        val sharedPref = LoginActivity.instance?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("IdUsuarioActivo", "")
            apply()
        }

        if(DataManager.progressDialog!!.isShowing) DataManager.progressDialog!!.dismiss()
    }


    override fun onBackPressed() {
        super.onBackPressed()

        moveTaskToBack(true)
        exitProcess(-1)
    }


}



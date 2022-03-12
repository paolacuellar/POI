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
import com.example.purrrfectpoi.Models.AuthModel
import com.example.purrrfectpoi.fragments.ChatsFragment
import com.example.purrrfectpoi.fragments.GruposFragment
import com.example.purrrfectpoi.fragments.MuroFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
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


    //var CorreoUsuario : String? = null
    //var PasswordUsuario : String? = null

    var authLogin : AuthModel = AuthModel()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
/*
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)
*/

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
        val emailUsuario = sharedPref.getString("emailUsuario", defaultValue)


        if(emailUsuario != ""){
            DataManager.emailUsuario = emailUsuario

            val vIntent =  Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }
    }


    fun logginUsuario(loginActivity: LoginActivity) {

        DataManager.progressDialog!!.setMessage("Ingresando")
        DataManager.progressDialog!!.setCancelable(false)
        DataManager.progressDialog!!.show()


        authLogin.Email = editTextEmail?.text.toString()
        authLogin.Password = editTextPassword?.text.toString()

        if(authLogin.Email.isEmpty() || authLogin.Password.isEmpty()){

            if(authLogin.Email.isEmpty()){
                editTextEmail?.setError("Inserte un correo valido")
            }
            if(authLogin.Password.isEmpty()){
                editTextPassword?.setError("Campo vacio")
            }
            if(DataManager.progressDialog!!.isShowing) DataManager.progressDialog!!.dismiss()
        }else{

            FirebaseAuth.getInstance ()
                .signInWithEmailAndPassword (
                    authLogin.Email,
                    authLogin.Password
                ).addOnCompleteListener() {
                    if (it.isSuccessful) {
                        DataManager.emailUsuario = authLogin.Email

                        val vIntent = Intent(this, MainActivity::class.java)
                        startActivity(vIntent)
                    } else {
                        DataManager.showAlert(this, "Hubo un error al iniciar sesión")
                        if(DataManager.progressDialog!!.isShowing) DataManager.progressDialog!!.dismiss()
                    }
                }
        }

    }


    override fun onResume() {
        super.onResume()

        val sharedPref = LoginActivity.instance?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("emailUsuario", "")
            FirebaseAuth.getInstance().signOut()
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



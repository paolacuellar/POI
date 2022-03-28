package com.example.purrrfectpoi

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.purrrfectpoi.Models.AuthModel
import com.google.firebase.auth.FirebaseAuth
import com.psm.hiring.Utils.DataManager
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

    var authLogin : AuthModel = AuthModel()


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

        //revisarUsuarioLoggeadoCache()
    }

    fun logginUsuario(loginActivity: LoginActivity) {

        DataManager.showProgressDialog("Ingresando")

        authLogin.Email = editTextEmail?.text.toString()
        authLogin.Password = editTextPassword?.text.toString()

        if(authLogin.Email.isEmpty() || authLogin.Password.isEmpty()){

            if(authLogin.Email.isEmpty()){
                editTextEmail?.setError("Inserte un correo valido")
            }
            if(authLogin.Password.isEmpty()){
                editTextPassword?.setError("Campo vacio")
            }
            DataManager.hideProgressDialog()
        }else{

            FirebaseAuth.getInstance ()
                .signInWithEmailAndPassword (
                    authLogin.Email,
                    authLogin.Password
                ).addOnCompleteListener() { responseSignUser ->
                    if (responseSignUser.isSuccessful) {

                        DataManager.emailUsuario = authLogin.Email

                        val sharedPref = loginActivity.getPreferences(Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString("emailUsuario", authLogin.Email)
                            apply()
                        }

                        if(DataManager.progressDialog!!.isShowing)
                            DataManager.progressDialog!!.dismiss()

                        val vIntent =  Intent(loginActivity, MainActivity::class.java)
                        startActivity(vIntent)

                    } else {
                        Log.d(TAG, "Exception: ${responseSignUser.exception!!.message}")

                        DataManager.showToast(this,"Error: ${responseSignUser.exception!!.message}")
                        DataManager.hideProgressDialog()
                    }
                }
        }

    }

    override fun onResume() {
        super.onResume()

        eliminarUsuarioLoggeadoCache()

    }

    override fun onBackPressed() {
        super.onBackPressed()

        moveTaskToBack(true)
        exitProcess(-1)
    }


    private fun revisarUsuarioLoggeadoCache() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = ""
        val emailUsuario = sharedPref.getString("emailUsuario", defaultValue)

        if(emailUsuario != ""){
            DataManager.emailUsuario = emailUsuario

            val vIntent =  Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }
    }


    private fun eliminarUsuarioLoggeadoCache() {

        val sharedPref = LoginActivity.instance?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("emailUsuario", "")
            FirebaseAuth.getInstance().signOut()
            apply()
        }

    }
}



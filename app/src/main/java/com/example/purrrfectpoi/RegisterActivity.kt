package com.example.purrrfectpoi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.purrrfectpoi.fragments.ChatsFragment
import com.example.purrrfectpoi.fragments.GruposFragment
import com.example.purrrfectpoi.fragments.MuroFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.psm.hiring.Utils.DataManager
import com.psm.hiring.Utils.DataManager.context
import java.util.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    val TAG="RegisterActivity"


    var btn_Register : Button? = null;
    var lbl_Login : TextView? = null;
    var header_back : View? = null;
    var editTextEmail : EditText? = null;
    var editTextPassword : EditText? = null;
    var editTextNombre : EditText? = null;
    var editTextApPaterno : EditText? = null;
    var editTextApMaterno : EditText? = null;
    var spinnerCarrera : Spinner? = null;




    var CorreoUsuario : String? = null;
    var PasswordUsuario : String? = null;
    var NombreUsuario : String? = null;
    var ApPaternoUsuario : String? = null;
    var ApMaternoUsuario : String? = null;
    var CarreraUsuario : String? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        this.btn_Register = findViewById<Button>(R.id.register_btn_register)
        this.lbl_Login = findViewById<TextView>(R.id.register_lbl_logguearse)
        this.header_back = findViewById<View>(R.id.register_header_back)

        this.editTextEmail = findViewById<EditText>(R.id.register_input_email)
        this.editTextPassword = findViewById<EditText>(R.id.register_input_pw)
        this.editTextNombre = findViewById<EditText>(R.id.register_input_nombre)
        this.editTextApPaterno = findViewById<EditText>(R.id.register_input_ap_paterno)
        this.editTextApMaterno = findViewById<EditText>(R.id.register_input_ap_materno)



        val carreras = resources.getStringArray(R.array.Carreras)

        this.spinnerCarrera = findViewById<Spinner>(R.id.register_spinner_carreras)

        if (this.spinnerCarrera != null) {
            val adapter = context?.let { ArrayAdapter(it, R.layout.item_chat, carreras) }
            this.spinnerCarrera!!.adapter = adapter
        }

        spinnerCarrera!!.onItemSelectedListener  = this


        this.btn_Register?.setOnClickListener {
            registerUsuario(this)

        }

        this.lbl_Login?.setOnClickListener {
            val vIntent = Intent(this, LoginActivity::class.java)
            startActivity(vIntent)
        }

        this.header_back?.setOnClickListener {
            val vIntent = Intent(this, LoginActivity::class.java)
            startActivity(vIntent)
        }


    }

    private fun registerUsuario(registerActivity: RegisterActivity) {

        DataManager.progressDialog!!.setMessage("Ingresando")
        DataManager.progressDialog!!.setCancelable(false)
        DataManager.progressDialog!!.show()


        CorreoUsuario = editTextEmail?.text.toString()
        PasswordUsuario = editTextPassword?.text.toString()
        NombreUsuario = editTextNombre?.text.toString()
        ApPaternoUsuario = editTextApPaterno?.text.toString()
        ApMaternoUsuario = editTextApMaterno?.text.toString()

        val validate_pass = isValidPassword(PasswordUsuario!!)

        if(     CorreoUsuario!!.isNullOrEmpty()
            ||  PasswordUsuario!!.isNullOrEmpty() || !validate_pass
            ||  NombreUsuario!!.isNullOrEmpty()
            ||  ApPaternoUsuario!!.isNullOrEmpty()
            ||  ApMaternoUsuario!!.isNullOrEmpty()
//            ||  CarreraUsuario!!.isNullOrEmpty()
        ){

            if(editTextEmail?.text.isNullOrEmpty()){
                editTextEmail?.setError("Inserte un correo valido")
            }
            if(editTextPassword?.text.isNullOrEmpty()){
                editTextPassword?.setError("Campo vacio")
            }
            if(!validate_pass){
                editTextPassword?.setError("La contraseña debe tener un número, minuscula, mayuscula, caracter especial y un tamaño de 8 caracteres minimo")
            }
            if(editTextNombre?.text.isNullOrEmpty()){
                editTextNombre?.setError("Campo vacio")
            }
            if(editTextApPaterno?.text.isNullOrEmpty()){
                editTextApPaterno?.setError("Campo vacio")
            }
            if(editTextApMaterno?.text.isNullOrEmpty()){
                editTextApMaterno?.setError("Campo vacio")
            }

            if(DataManager.progressDialog!!.isShowing) DataManager.progressDialog!!.dismiss()
        }else{
            val vIntent =  Intent(this, LoginActivity::class.java)
            startActivity(vIntent)
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.CarreraUsuario = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    fun isValidPassword(password : String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

}



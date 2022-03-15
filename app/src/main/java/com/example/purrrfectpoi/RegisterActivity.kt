package com.example.purrrfectpoi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager
import java.util.*
import android.widget.ArrayAdapter
import com.example.purrrfectpoi.Models.AuthModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.google.firebase.auth.FirebaseAuth


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

    var usuarioRegister : UsuariosModel = UsuariosModel()
    var authRegister : AuthModel = AuthModel()

    private lateinit var db : FirebaseFirestore

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

        var carrerasFCFM : ArrayList<String> = arrayListOf()
        db = FirebaseFirestore.getInstance()
        db.collection("Carrera").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    carrerasFCFM.add(document.data.get("Nombre").toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error consiguiendo Carreras", exception)
                DataManager.showToast(this, "Error consiguiendo Carreras")
            }

        val adapter = ArrayAdapter(this, R.layout.item_spinner, carrerasFCFM)

        this.spinnerCarrera = findViewById<Spinner>(R.id.register_spinner_carreras)


        if (this.spinnerCarrera != null) {
            spinnerCarrera!!.setAdapter(adapter)
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

        authRegister.Email = editTextEmail?.text.toString()
        authRegister.Password = editTextPassword?.text.toString()

        usuarioRegister.Email = authRegister.Email
        usuarioRegister.Nombre = editTextNombre?.text.toString()
        usuarioRegister.ApPaterno = editTextApPaterno?.text.toString()
        usuarioRegister.ApMaterno = editTextApMaterno?.text.toString()

        //usuarioRegister.Carrera = "5jUhUWbOUD9Yii3keJQ4"
        var Carrera = "5jUhUWbOUD9Yii3keJQ4"
        usuarioRegister.Carrera =  db.document("Carrera/${Carrera}")

        val validate_pass = isValidPassword(authRegister.Password)

        if(     authRegister.Email.isEmpty()
            ||  authRegister.Password.isEmpty() || !validate_pass
            ||  usuarioRegister.Nombre.isEmpty()
            ||  usuarioRegister.ApPaterno.isEmpty()
            ||  usuarioRegister.ApMaterno.isEmpty()
            ||  Carrera.isEmpty()
        ){

            if(authRegister.Email.isEmpty()){
                editTextEmail?.setError("Inserte un correo valido")
            }
            else{
                //TODO: HAY QUE VALIDAR SI EL CORREO ESTA REPETIDO? O YA QUE FIREBASE LO HAGA POR NOSOTROS?
            }
            if(authRegister.Password.isEmpty()){
                editTextPassword?.setError("Campo vacio")
            }
            if(!validate_pass){
                editTextPassword?.setError("La contraseña debe tener un número, minuscula, mayuscula, caracter especial y un tamaño de 8 caracteres minimo")
            }
            if(usuarioRegister.Nombre.isEmpty()){
                editTextNombre?.setError("Campo vacio")
            }
            if(usuarioRegister.ApPaterno.isEmpty()){
                editTextApPaterno?.setError("Campo vacio")
            }
            if(usuarioRegister.ApMaterno.isEmpty()){
                editTextApMaterno?.setError("Campo vacio")
            }
            if(Carrera.isEmpty()){
                DataManager.showToast(this, "Error: \"Carrera\" no seleccionada")
            }

        }else{
            DataManager.showProgressDialog("Creando usuario")

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                authRegister.Email,
                authRegister.Password
            ).addOnCompleteListener{ responseAuthCreation ->
                if (responseAuthCreation.isSuccessful){

                    db.collection("Usuarios").document(usuarioRegister.Email)
                        .set(
                            hashMapOf(
                                "Nombre" to usuarioRegister.Nombre,
                                "ApPaterno" to usuarioRegister.ApPaterno,
                                "ApMaterno" to usuarioRegister.ApMaterno,
                                "Foto" to usuarioRegister.Foto,
                                "Carrera" to usuarioRegister.Carrera,
                                "Conversaciones" to usuarioRegister.Conversaciones,
                                "Conectado" to usuarioRegister.Conectado,
                                "CantidadTareas" to usuarioRegister.CantidadTareas,
                                "CantidadGrupos" to usuarioRegister.CantidadGrupos,
                                "CantidadPosts" to usuarioRegister.CantidadPosts
                            )
                        ).addOnCompleteListener{ responseUserCreation ->
                            if (responseUserCreation.isSuccessful) {
                                val vIntent = Intent(this, LoginActivity::class.java)
                                startActivity(vIntent)
                            }
                            else{
                                DataManager.showToast(this,"Error: ${responseUserCreation.exception!!.message}")
                            }
                        }
                }
                else{
                    DataManager.showToast(this,"Error: ${responseAuthCreation.exception!!.message}")
                }
            }





        }

    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d(TAG, "ENTRO EN EL ITEM SELECTED")
        var CarreraUsuario = parent?.getItemAtPosition(position).toString()

        db.collection("Carrera").whereEqualTo("Nombre", CarreraUsuario).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    fun isValidPassword(password : String): Boolean {
        /*
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return matcher.matches()
        */

        return true
    }

}



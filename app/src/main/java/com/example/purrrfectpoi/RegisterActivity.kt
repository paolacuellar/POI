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
    private var spinnerCarrera : Spinner? = null;
    private var carrerasFCFM: ArrayList<String>? = null
    private var adapter: ArrayAdapter<String>? = null


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

        spinnerCarrera = findViewById<View>(R.id.register_spinner_carreras) as Spinner

        //val carreras = arrayOf("LMAD", "LA", "LCC", "LSTI", "LM", "LF")

//SPINNER LUISITO
        /*var carrerasFCFM : ArrayList<String> = arrayListOf()
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

        spinnerCarrera!!.onItemSelectedListener  = this*/

        spinnerCarrera!!.onItemSelectedListener  = this

        carrerasFCFM = ArrayList<String>()
        adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            carrerasFCFM!!
        )
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCarrera!!.setAdapter(adapter)

        getData()


        this.btn_Register?.setOnClickListener {
            registerUsuario()

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

    private fun registerUsuario() {

        authRegister.Email = editTextEmail?.text.toString()
        authRegister.Password = editTextPassword?.text.toString()

        usuarioRegister.Email = authRegister.Email
        usuarioRegister.Nombre = editTextNombre?.text.toString()
        usuarioRegister.ApPaterno = editTextApPaterno?.text.toString()
        usuarioRegister.ApMaterno = editTextApMaterno?.text.toString()

        /*
        var carreaSeleccionada= spinnerCarrera!!.selectedItem.toString()
        db.collection("Carrera").whereEqualTo("Nombre", carreaSeleccionada).get()
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
        */


        var CarreraSeleccionada = spinnerCarrera!!.selectedItem.toString()

        val validate_pass = isValidPassword(authRegister.Password)

        if(     authRegister.Email.isEmpty()
            ||  authRegister.Password.isEmpty() || !validate_pass
            ||  usuarioRegister.Nombre.isEmpty()
            ||  usuarioRegister.ApPaterno.isEmpty()
            ||  usuarioRegister.ApMaterno.isEmpty()
            ||  CarreraSeleccionada.isEmpty()
        ){

            if(authRegister.Email.isEmpty()){
                editTextEmail?.setError("Inserte un correo valido")
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
            if(CarreraSeleccionada.isEmpty()){
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
                                "CantidadPosts" to usuarioRegister.CantidadPosts,
                                "Ecriptado" to usuarioRegister.Ecriptado
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

    fun getData(){
        db = FirebaseFirestore.getInstance()
        db.collection("Carrera").get()
            .addOnSuccessListener { documents ->
                carrerasFCFM?.clear()
                for (document in documents) {
                    carrerasFCFM?.add(document.data.get("Nombre").toString())
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error consiguiendo Carreras", exception)
                DataManager.showToast(this, "Error consiguiendo Carreras")
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d(TAG, "ENTRO EN EL ITEM SELECTED")
        var CarreraUsuario = parent?.getItemAtPosition(position).toString()

        db.collection("Carrera").whereEqualTo("Nombre", CarreraUsuario).get()
            .addOnSuccessListener { documentsCarreras ->
                for (documentsCarrera in documentsCarreras) {
                    Log.d(TAG, "${documentsCarrera.id} => ${documentsCarrera.data}")
                    usuarioRegister.Carrera = db.document("Carrera/${documentsCarrera.id}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //TODO("Not yet implemented")
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



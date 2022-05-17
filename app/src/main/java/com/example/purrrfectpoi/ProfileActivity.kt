package com.example.purrrfectpoi

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.MedallasModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.adapters.MedallasAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import java.util.*
import android.widget.CompoundButton
import androidx.recyclerview.widget.GridLayoutManager


class ProfileActivity : AppCompatActivity() {

    val TAG="ProfileActivity"

    var btn_Editar : Button? = null;
    var btn_Editar_Foto : ImageView? = null;
    var button_log_out : View? = null;
    var navbar_back : ImageView? = null;
    var switch_Encriptar : Switch? = null;

    var image_Foto : ImageView? = null;
    var textCorreo : TextView? = null;
    var textCarrera : TextView? = null;
    var editTextNombre : EditText? = null;
    var editTextApPaterno : EditText? = null;
    var editTextApMaterno : EditText? = null;

    private lateinit var recyclerViewMedallas : RecyclerView
    private lateinit var medallasAdapter: MedallasAdapter

    var usuarioProfile : UsuariosModel = UsuariosModel()
    var editable:Boolean = false

    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.btn_Editar = findViewById<Button>(R.id.profile_btn_editar)
        this.btn_Editar_Foto = findViewById<ImageView>(R.id.profile_image_btn_editar_foto)
        this.navbar_back = findViewById<ImageView>(R.id.profile_image_btn_back)
        this.button_log_out = findViewById<View>(R.id.profile_button_log_out)
        this.switch_Encriptar = findViewById<Switch>(R.id.Switch_EcryptName)

        this.image_Foto = findViewById<ImageView>(R.id.profile_image_foto)
        this.textCorreo = findViewById<TextView>(R.id.profile_lbl_correo)
        this.textCarrera = findViewById<TextView>(R.id.profile_lbl_carrera)
        this.editTextNombre = findViewById<EditText>(R.id.profile_input_nombre)
        this.editTextApPaterno = findViewById<EditText>(R.id.profile_input_ap_paterno)
        this.editTextApMaterno = findViewById<EditText>(R.id.profile_input_ap_materno)
        this.recyclerViewMedallas = findViewById<RecyclerView>(R.id.profile_rv_medallas)

        habilitarBotones(false)

        setUpInfoProfile()

        this.btn_Editar?.setOnClickListener {
            editarUsuario()
        }

        this.btn_Editar_Foto?.setOnClickListener {
            editarFotoUsuario()
        }

        this.navbar_back?.setOnClickListener {
            val vIntent = Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }

        this.button_log_out?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            DataManager.updateUserConnected(false)

            DataManager.emailUsuario = ""
            DataManager.fotoUsuario = ""
            val vIntent = Intent(this, LoginActivity::class.java)
            startActivity(vIntent)
        }

        switch_Encriptar!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                DataManager.showToast(this, "Encriptacion Activada")
                usuarioProfile.Ecriptado = isChecked
                usuarioProfile.EcriptarInfo()
            } else {
                DataManager.showToast(this, "Encriptacion Desactivada")
                usuarioProfile.DesencriptarInfo()
                usuarioProfile.Ecriptado = isChecked
            }

            if (DataManager.emailUsuario != null) {
                if (DataManager.emailUsuario != "") {
                    db.collection("Usuarios").document(DataManager.emailUsuario!!)
                        .update(
                            mapOf(
                                "Nombre" to usuarioProfile.Nombre,
                                "ApPaterno" to usuarioProfile.ApPaterno,
                                "ApMaterno" to usuarioProfile.ApMaterno,
                                "Ecriptado" to usuarioProfile.Ecriptado
                            )
                        ).addOnCompleteListener {
                            if (!it.isSuccessful) {
                                DataManager.showAlert(this, "Error:" + it.exception?.message)
                            }
                        }
                }
            }
        })

    }

    private fun setUpInfoProfile() {
        if (DataManager.emailUsuario != null) {
            db = FirebaseFirestore.getInstance()
            db.collection("Usuarios").document(DataManager.emailUsuario!!).get()
                .addOnSuccessListener { responseUsuario ->

                    usuarioProfile.Email = responseUsuario.id
                    usuarioProfile.Carrera = if (responseUsuario.get("Carrera") != null) responseUsuario.get("Carrera") as DocumentReference else null
                    usuarioProfile.Nombre = if (responseUsuario.get("Nombre") != null) responseUsuario.get("Nombre") as String else ""
                    usuarioProfile.ApPaterno = if (responseUsuario.get("ApPaterno") != null) responseUsuario.get("ApPaterno") as String else ""
                    usuarioProfile.ApMaterno = if (responseUsuario.get("ApMaterno") != null) responseUsuario.get("ApMaterno") as String else ""
                    usuarioProfile.CantidadTareas = if (responseUsuario.get("CantidadTareas") != null) responseUsuario.get("CantidadTareas") as Long else 0
                    usuarioProfile.CantidadGrupos = if (responseUsuario.get("CantidadGrupos") != null) responseUsuario.get("CantidadGrupos") as Long else 0
                    usuarioProfile.CantidadPosts = if (responseUsuario.get("CantidadPosts") != null) responseUsuario.get("CantidadPosts") as Long else 0
                    usuarioProfile.Foto = if (responseUsuario.get("Foto") != null) responseUsuario.get("Foto") as String else ""
                    usuarioProfile.Ecriptado = if (responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false
                    usuarioProfile.Ecriptado = if (responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false

                    usuarioProfile.DesencriptarInfo()

                    db.collection("Carrera").document(usuarioProfile.Carrera!!.id).get()
                        .addOnSuccessListener { responseCarrera ->
                            var carreraUsuario = if (responseCarrera.get("Nombre") != null) responseCarrera.get("Nombre") as String else ""
                            textCarrera?.text = carreraUsuario
                            textCorreo?.text = usuarioProfile.Email
                            editTextNombre?.setText(usuarioProfile.Nombre)
                            editTextApPaterno?.setText(usuarioProfile.ApPaterno)
                            editTextApMaterno?.setText(usuarioProfile.ApMaterno)

                            switch_Encriptar!!.isChecked = usuarioProfile.Ecriptado
                        }



                    setUpImageUser()

                    setupRecyclerView()
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error al conseguir la información del Usuario", exception)
                }
        }
    }

    private fun editarUsuario() {

        if (!editable){
            habilitarBotones(true)
        }
        else{
            usuarioProfile.Nombre = editTextNombre?.text.toString()
            usuarioProfile.ApPaterno = editTextApPaterno?.text.toString()
            usuarioProfile.ApMaterno = editTextApMaterno?.text.toString()

            if(     usuarioProfile.Nombre.isEmpty()
                ||  usuarioProfile.ApPaterno.isEmpty()
                ||  usuarioProfile.ApMaterno.isEmpty()
            ){

                if(usuarioProfile.Nombre.isEmpty()){
                    editTextNombre?.setError("Campo vacio")
                }
                if(usuarioProfile.ApPaterno.isEmpty()){
                    editTextApPaterno?.setError("Campo vacio")
                }
                if(usuarioProfile.ApMaterno.isEmpty()){
                    editTextApMaterno?.setError("Campo vacio")
                }

            }else{

                usuarioProfile.EcriptarInfo()

                DataManager.showProgressDialog("Editando Perfil")
                db.collection("Usuarios").document(DataManager.emailUsuario!!)
                    .update(
                        mapOf(
                            "Nombre" to usuarioProfile.Nombre,
                            "ApPaterno" to usuarioProfile.ApPaterno,
                            "ApMaterno" to usuarioProfile.ApMaterno
                        )
                    ).addOnCompleteListener{
                        if (it.isSuccessful) {
                            habilitarBotones(false)

                            Toast.makeText(applicationContext, "Se ha editado con exito la información", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            DataManager.showAlert(this, "Se ha producido un error al editar el usuario")
                        }
                    }
            }
        }
    }

    private fun editarFotoUsuario () {
        var i = Intent ()
        i.setType ("image/*")
        i.setAction (Intent.ACTION_GET_CONTENT)
        startActivityForResult (Intent.createChooser (i, "Choose Picture"), 111)
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null)
        {
            //Actualizar ImageView
            var filepath = data!!.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            image_Foto!!.setImageBitmap(bitmap)

            //Subir Imagen a Firebase Storage y Actualizar campo "Foto" del Usuario
            DataManager.showProgressDialog("Subiendo imagen")

            var editarPathFotoUsuario = false
            if (usuarioProfile.Foto.isEmpty()) {
                usuarioProfile.Foto = UUID.randomUUID().toString() + ".jpg"
                editarPathFotoUsuario = true
            }

            var pathImage = "images/Usuarios/${usuarioProfile.Foto}"

            var imageRef = FirebaseStorage.getInstance().reference.child( pathImage)

            imageRef.putFile (filepath)
                .addOnSuccessListener { responseImageUpload ->
                    if (editarPathFotoUsuario){

                        db.collection("Usuarios").document(DataManager.emailUsuario!!)
                            .update(
                                mapOf(
                                    "Foto" to usuarioProfile.Foto
                                )
                            ).addOnCompleteListener{
                                if (it.isSuccessful) {
                                    DataManager.hideProgressDialog()
                                    Toast.makeText(applicationContext, "Foto del Usuario editada correctamente", Toast.LENGTH_LONG).show()
                                }
                                else{
                                    DataManager.hideProgressDialog()
                                    DataManager.showAlert(this, "Se ha producido un error al editar el usuario")
                                }
                            }
                    }
                    else{
                        DataManager.hideProgressDialog()
                        Toast.makeText(applicationContext, "Foto del Usuario editada correctamente", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { responseImageUpload ->
                    DataManager.hideProgressDialog()
                    Toast.makeText(applicationContext, responseImageUpload.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { responseImageUpload ->
                    var progressPercent = (100.0 * responseImageUpload.bytesTransferred) / responseImageUpload.totalByteCount
                    DataManager.updateMessage("Uploaded ${progressPercent.toInt()}%")
                }

        }
    }

    private fun setUpImageUser(){
        if (usuarioProfile.Foto.isNotEmpty()) {
            
            FirebaseStorage.getInstance().getReference("images/Usuarios/${usuarioProfile.Foto}").downloadUrl
                .addOnSuccessListener { pathImageUser ->

                    Glide.with(this)
                        .load(pathImageUser.toString())
                        .into(image_Foto!!)
                }
        }
        else {
            image_Foto!!.setImageResource(R.drawable.foto_default_perfil)
        }
    }

    private fun setupRecyclerView(){
        db.collection("Medallas")
            .orderBy("Tipo")
            .orderBy("CantidadRequerida", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { responseMedallas ->

                var listaMedallas : MutableList<MedallasModel> = mutableListOf()

                for (responseMedalla in responseMedallas) {
                    var medallaAux = MedallasModel()
                    medallaAux.id = responseMedalla.id
                    medallaAux.Nombre = responseMedalla.data.get("Nombre") as String
                    medallaAux.Foto = responseMedalla.data.get("Foto") as String
                    medallaAux.Tipo = responseMedalla.data.get("Tipo") as String
                    medallaAux.CantidadRequerida = (responseMedalla.data.get("CantidadRequerida") as Long).toInt()

                    medallaAux.MedallaObtenida =
                    when (medallaAux.Tipo) {
                        "Post" -> if (usuarioProfile.CantidadPosts >= medallaAux.CantidadRequerida) true else false
                        "Grupo" -> if (usuarioProfile.CantidadGrupos >= medallaAux.CantidadRequerida) true else false
                        "Tarea" -> if (usuarioProfile.CantidadTareas >= medallaAux.CantidadRequerida) true else false
                        else -> false
                    }

                    listaMedallas.add(medallaAux)
                }

                medallasAdapter = MedallasAdapter(listaMedallas)
                recyclerViewMedallas.apply {
                    adapter = medallasAdapter
                    layoutManager = GridLayoutManager(context,3)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al conseguir las medallas", exception)
            }
    }

    private fun habilitarBotones(enable : Boolean) {
        this.editable = enable

        this.editTextNombre!!.isEnabled = enable
        this.editTextApPaterno!!.isEnabled = enable
        this.editTextApMaterno!!.isEnabled = enable

        var background : Int? = null
        var color : Int? = null
        if (enable) {
            background = R.drawable.search_background
            color = R.color.black
        }
        else {
            background = R.drawable.search_background_blocked
            color = R.color.gray
        }

        this.editTextNombre!!.background = ContextCompat.getDrawable(this,background)
        this.editTextApPaterno!!.background = ContextCompat.getDrawable(this, background)
        this.editTextApMaterno!!.background = ContextCompat.getDrawable(this, background)


        this.editTextNombre!!.setTextColor(ContextCompat.getColor(this, color))
        this.editTextApPaterno!!.setTextColor(ContextCompat.getColor(this, color))
        this.editTextApMaterno!!.setTextColor(ContextCompat.getColor(this, color))

    }
}



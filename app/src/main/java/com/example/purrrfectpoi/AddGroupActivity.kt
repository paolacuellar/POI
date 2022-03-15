package com.example.purrrfectpoi

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.GruposModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.adapters.MedallasAdapter
import com.example.purrrfectpoi.adapters.UsuariosGruposAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import com.psm.hiring.Utils.DataManager.context
import java.util.*

class AddGroupActivity : AppCompatActivity() {

    var btn_Crear_Grupo : Button? = null;
    var header_back : View? = null;

    var editTextNombre : EditText? = null;
    var editTextDescripcion : EditText? = null;
    var image_Foto : ImageView? = null;

    var grupoCreado : GruposModel = GruposModel()

    lateinit var filepath : Uri
    var editarPathFotoUsuario = false
    
    private lateinit var recyclerViewUsuarios : RecyclerView
    private lateinit var usuariosAdapter: UsuariosGruposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        this.btn_Crear_Grupo = findViewById<Button>(R.id.register_btn_register)
        this.header_back = findViewById<View>(R.id.register_header_back)
        this.image_Foto = findViewById<ImageView>(R.id.profile_image_foto)

        this.editTextNombre = findViewById<EditText>(R.id.register_input_email)
        this.editTextDescripcion = findViewById<EditText>(R.id.register_input_nombre)
        this.recyclerViewUsuarios = findViewById<RecyclerView>(R.id.profile_rv_medallas)
        
        btn_Crear_Grupo!!.setOnClickListener{
            crearEditarGrupo()
        }

        image_Foto!!.setOnClickListener{
            editarFotoUsuario()
        }

        val bundle = intent.extras
        val grupoId = bundle!!.getString("grupoId")

        setUpInfoCurso(grupoId!!)

    }

    private fun setUpInfoCurso(grupoId : String) {

        FirebaseFirestore.getInstance().collection("Grupos").document(grupoId).get()

            .addOnSuccessListener {

                //TODO: NO ES TAN NECESARIO TRAERNOS LA INFO DE "CONVERSACION" Y "TAREAS", ESTA BIEN DE TODOS MODOS TRAERLA?
                grupoCreado.Nombre = if(it.get("Nombre") != null) it.get("Nombre") as String else ""
                grupoCreado.Descripcion = if(it.get("Descripcion") != null) it.get("Descripcion") as String else ""
                grupoCreado.Foto = if(it.get("Foto") != null) it.get("Foto") as String else ""
                grupoCreado.Creador = if(it.get("Creador") != null) it.get("Creador") as DocumentReference else null
                grupoCreado.Miembros = if(it.get("Miembros") != null) it.get("Miembros") as ArrayList<DocumentReference> else arrayListOf()
                grupoCreado.Conversacion = if(it.get("Conversacion") != null) it.get("Conversacion") as DocumentReference else null
                grupoCreado.Tareas = if(it.get("Tareas") != null)  it.get("Tareas") as ArrayList<DocumentReference> else arrayListOf()

                editTextNombre?.setText(grupoCreado.Nombre)
                editTextDescripcion?.setText(grupoCreado.Descripcion)

                setUpImageGrupo()

                setupMiembrosRecyclerView()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    private fun setUpImageGrupo() {
        if (grupoCreado.Foto.isNotEmpty()) {

            FirebaseStorage.getInstance().getReference("images/Grupos/${grupoCreado.Foto}").downloadUrl
                .addOnSuccessListener {

                    Glide.with(this)
                        .load(it.toString())
                        .into(image_Foto!!)
                }
        }
    }
    
    private fun setupMiembrosRecyclerView(){
        if (grupoCreado.Miembros.isNotEmpty()) {

            //TODO: PARECE QUE EL "whereIn" NOMAS JALA CON 10 USUARIOS, SI ES ASÍ DEBERE HACER UN CICLO FOR DE CONSULTAS
            FirebaseFirestore.getInstance().collection("Usuarios")
                .whereIn("Miembros", grupoCreado.Miembros)
                .get()
                .addOnSuccessListener { responseUsuarios ->

                    var listaUsuarios: MutableList<UsuariosModel> = mutableListOf()

                    for (responseUsuario in responseUsuarios) {
                        var usuarioAux = UsuariosModel()

                        usuarioAux.Email = responseUsuario.id
                        usuarioAux.Nombre = responseUsuario.data.get("Nombre") as String
                        usuarioAux.Nombre = responseUsuario.data.get("Nombre") as String
                        usuarioAux.Foto = responseUsuario.data.get("Foto") as String
                        
                        listaUsuarios.add(usuarioAux)
                    }

                    usuariosAdapter = UsuariosGruposAdapter(listaUsuarios)
                    recyclerViewUsuarios.apply {
                        adapter = usuariosAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error al conseguir las medallas", exception)
                }
        }
    }
    
    private fun agregarUsuarioAMiembros() {
        var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)
        grupoCreado.Miembros.add(documentReferenceUserLogged)
    }

    private fun crearEditarGrupo() {
        grupoCreado.Nombre = editTextNombre?.text.toString()
        grupoCreado.Descripcion = editTextDescripcion?.text.toString()

        if(     grupoCreado.Nombre.isEmpty()
            ||  grupoCreado.Descripcion.isEmpty()
        ){

            if(grupoCreado.Nombre.isEmpty()){
                editTextNombre?.setError("Campo vacio")
            }

            if(grupoCreado.Descripcion.isEmpty()){
                editTextDescripcion?.setError("Campo vacio")
            }

        }else{
            var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)
            grupoCreado.Creador = documentReferenceUserLogged

            DataManager.showProgressDialog("Creando usuario")

            if (grupoCreado.Foto.isEmpty()) {
                grupoCreado.Foto = UUID.randomUUID().toString() + ".jpg"
            }

            var pathImage = "images/Grupos/${grupoCreado.Foto}"

            FirebaseFirestore.getInstance().collection("Grupos").document()
                .update(
                    mapOf(
                        "Nombre" to grupoCreado.Nombre,
                        "Descripcion" to grupoCreado.Descripcion,
                        "Foto" to grupoCreado.Foto,
                        "Creador" to grupoCreado.Creador,
                        "Miembros" to grupoCreado.Miembros,
                        "Conversacion" to grupoCreado.Conversacion,
                        "Tareas" to grupoCreado.Tareas
                    )
                ).addOnCompleteListener{ responseUserCreation ->
                    if (responseUserCreation.isSuccessful) {

                        if (editarPathFotoUsuario) {
                            var imageRef = FirebaseStorage.getInstance().reference.child(pathImage)

                            imageRef.putFile(filepath)
                                .addOnSuccessListener { responseImageUpload ->
                                    val vIntent = Intent(this, LoginActivity::class.java)
                                    startActivity(vIntent)
                                }
                                .addOnFailureListener { responseImageUpload ->
                                    DataManager.hideProgressDialog()
                                    Toast.makeText(
                                        applicationContext,
                                        responseImageUpload.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                .addOnProgressListener { responseImageUpload ->
                                    var progressPercent = (100.0 * responseImageUpload.bytesTransferred) / responseImageUpload.totalByteCount
                                    DataManager.updateMessage("Uploaded ${progressPercent.toInt()}%")
                                }
                        }
                        else {
                            val vIntent = Intent(this, LoginActivity::class.java)
                            startActivity(vIntent)
                        }
                    }
                    else{
                        DataManager.showToast(this,"Error: ${responseUserCreation.exception!!.message}")
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

            editarPathFotoUsuario = true
        }
    }

}
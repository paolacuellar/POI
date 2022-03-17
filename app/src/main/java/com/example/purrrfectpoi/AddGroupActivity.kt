package com.example.purrrfectpoi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.GruposModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.adapters.UsuariosGruposAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import java.util.*

class AddGroupActivity : AppCompatActivity() {

    var btn_Crear_Grupo : Button? = null;
    var header_back : View? = null;

    var editTextAddMember : EditText? = null;
    var btn_add_member : FloatingActionButton? = null
    var editTextNombre : EditText? = null;
    var editTextDescripcion : EditText? = null;
    var image_Foto : ImageView? = null;
    var btn_image_Foto : FloatingActionButton? = null;

    var grupoCreado : GruposModel = GruposModel()

    var filepath : Uri? = null
    var editarPathFotoUsuario = false
    
    private lateinit var recyclerViewUsuarios : RecyclerView
    private lateinit var usuariosAdapter: UsuariosGruposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_ac)

        this.btn_Crear_Grupo = findViewById<Button>(R.id.add_group_btn_confirm)
        this.header_back = findViewById<View>(R.id.add_group_header_back)
        this.image_Foto = findViewById<ImageView>(R.id.add_group_image_foto)
        this.btn_image_Foto = findViewById<FloatingActionButton>(R.id.add_group_btn_image_foto)

        this.editTextAddMember = findViewById<EditText>(R.id.add_group_input_add_member)
        this.btn_add_member = findViewById<FloatingActionButton>(R.id.add_group_btn_add_member)

        this.editTextNombre = findViewById<EditText>(R.id.add_group_input_nombre)
        this.editTextDescripcion = findViewById<EditText>(R.id.add_group_input_descripcion)
        this.recyclerViewUsuarios = findViewById<RecyclerView>(R.id.add_group_scrollview_miembros)

        var listaUsuarios: MutableList<UsuariosModel> = mutableListOf()
        usuariosAdapter = UsuariosGruposAdapter(listaUsuarios)
        recyclerViewUsuarios.apply {
            adapter = usuariosAdapter
            layoutManager = LinearLayoutManager(context)
        }

        header_back!!.setOnClickListener{
            val vIntent = Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }

        btn_image_Foto!!.setOnClickListener{
            editarFotoUsuario()
        }

        btn_Crear_Grupo!!.setOnClickListener{
            crearEditarGrupo()
        }

        btn_add_member!!.setOnClickListener {
            agregarUsuarioAMiembros()
        }

        if (intent.hasExtra("grupoId")) {
        //if (true){
            val bundle = intent.extras
            val grupoId = bundle!!.getString("grupoId")
            //val grupoId = "P7PhnW548H1IRSCnig6m"

            setUpInfoCurso(grupoId!!)
        }
        else{
            image_Foto!!.setImageResource(R.drawable.foto_default_perfil)
        }
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

            //TODO: PARECE QUE EL "whereIn" NOMAS JALA CON 10 USUARIOS, SI ES ASÍ DEBERE HACER UN CICLO FOR DE CONSULTAS, O DEBERE MANEJARLO DE OTRA MANERA?
            FirebaseFirestore.getInstance().collection("Usuarios")
                .whereIn(FieldPath.documentId(), grupoCreado.Miembros)
                .get()
                .addOnSuccessListener { responseUsuarios ->
                    var listaUsuarios: MutableList<UsuariosModel> = mutableListOf()

                    for (responseUsuario in responseUsuarios) {
                        var usuarioAux = UsuariosModel()

                        if (responseUsuario.id != DataManager.emailUsuario) {
                            usuarioAux.Email = responseUsuario.id
                            usuarioAux.Nombre = responseUsuario.data.get("Nombre") as String
                            usuarioAux.Nombre = responseUsuario.data.get("Nombre") as String
                            usuarioAux.Foto = responseUsuario.data.get("Foto") as String

                            listaUsuarios.add(usuarioAux)

                            usuariosAdapter = UsuariosGruposAdapter(listaUsuarios)
                            usuariosAdapter.notifyDataSetChanged()
                        }
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error al conseguir las medallas", exception)
                }
        }



    }
    
    private fun agregarUsuarioAMiembros() {
        var userEmail : String = this.editTextAddMember!!.text.toString()
        var isEmail = true

        if(userEmail.isEmpty() || !isEmail) {
            if(userEmail.isEmpty() || !isEmail){
                editTextAddMember?.setError("Inserte un correo valido")
            }
        }
        else{
            if (!usuariosAdapter.isItemAdded(userEmail)) {
                FirebaseFirestore.getInstance().collection("Usuarios")
                    .document(userEmail)
                    .get()
                    .addOnSuccessListener { responseUsuario ->

                        if (responseUsuario.data != null) {
                            var usuarioAux = UsuariosModel()

                            if (responseUsuario.id != DataManager.emailUsuario) {
                                usuarioAux.Email = responseUsuario.id
                                usuarioAux.Nombre = responseUsuario.data!!.get("Nombre") as String
                                usuarioAux.Foto = responseUsuario.data!!.get("Foto") as String

                                usuariosAdapter.addItem(usuarioAux)

                                this.editTextAddMember!!.setText("")
                            }
                            else
                                DataManager.showToast(this, "El usuario ya fue agregado")
                        }
                        else
                            DataManager.showToast(this, "No se encontro al usuario \"${userEmail}\"")

                    }
                    .addOnFailureListener { exception ->
                        DataManager.showToast(this, "Hubo un error al buscar al usuario \"${userEmail}\" Excepcion: ${exception.message}")
                    }
            }
            else
                DataManager.showToast(this, "El usuario creador ya es miembro del grupo")


        }

    }

    private fun crearEditarGrupo() {
        grupoCreado.Nombre = editTextNombre?.text.toString()
        grupoCreado.Descripcion = editTextDescripcion?.text.toString()

        var listaMiembros : MutableList<UsuariosModel>? = arrayListOf()
        if (!usuariosAdapter.listaUsuariosGrupo.isEmpty())
            listaMiembros = usuariosAdapter.listaUsuariosGrupo



        if(     grupoCreado.Nombre.isEmpty()
            ||  grupoCreado.Descripcion.isEmpty()
            //||  listaMiembros!!.count() < 5
            ||  filepath == null
        ){

            if(grupoCreado.Nombre.isEmpty()){
                editTextNombre?.setError("Campo vacio")
            }

            if(grupoCreado.Descripcion.isEmpty()){
                editTextDescripcion?.setError("Campo vacio")
            }

            if(listaMiembros!!.count() < 5){
                DataManager.showToast(this, "Error, los miembros del grupo deben ser mayores a 5")
            }


            if(filepath == null){
                DataManager.showToast(this, "Error, falta agregar una foto para el grupo")
            }

        }else{
            var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)
            grupoCreado.Creador = documentReferenceUserLogged

            grupoCreado.Miembros.add(grupoCreado.Creador!!)

            for (miembroGrupo in listaMiembros!!){
                var documentReferenceMiembro = FirebaseFirestore.getInstance().collection("Usuarios").document(miembroGrupo.Email)
                grupoCreado.Miembros.add(documentReferenceMiembro)
            }

            DataManager.showProgressDialog("Creando usuario")

            if (grupoCreado.Foto.isEmpty()) {
                grupoCreado.Foto = UUID.randomUUID().toString() + ".jpg"
            }

            var pathImage = "images/Grupos/${grupoCreado.Foto}"

            grupoCreado.id = FirebaseFirestore.getInstance().collection("Grupos").document().id

            FirebaseFirestore.getInstance().collection("Grupos").document(grupoCreado.id)
                .set(
                    hashMapOf(
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

                            imageRef.putFile(filepath!!)
                                .addOnSuccessListener { responseImageUpload ->
                                    val vIntent = Intent(this, MainActivity::class.java)
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
            filepath = data!!.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            image_Foto!!.setImageBitmap(bitmap)

            editarPathFotoUsuario = true
        }
    }

}
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
    var subirFotoGrupo = false
    var carreraUsuario : DocumentReference? = null

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

        setCarreraUsuario()

        header_back!!.setOnClickListener{
            onBackPressed()
        }

        btn_image_Foto!!.setOnClickListener{
            subirFotoGrupo()
        }

        btn_Crear_Grupo!!.setOnClickListener{
            ingresarInfoGrupo()
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

    private fun setCarreraUsuario() {
        FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!).get()
            .addOnSuccessListener { responseUsuario ->
                carreraUsuario =     if(responseUsuario.get("Carrera") != null)    responseUsuario.get("Carrera") as DocumentReference else null
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    private fun setUpInfoCurso(grupoId : String) {

        FirebaseFirestore.getInstance().collection("Grupos").document(grupoId).get()

            .addOnSuccessListener {

                //TODO: NO ES TAN NECESARIO TRAERNOS LA INFO DE "CONVERSACION" Y "TAREAS", ESTA BIEN DE TODOS MODOS TRAERLA?
                grupoCreado.id = grupoId
                grupoCreado.Nombre = if(it.get("Nombre") != null) it.get("Nombre") as String else ""
                grupoCreado.Descripcion = if(it.get("Descripcion") != null) it.get("Descripcion") as String else ""
                grupoCreado.Foto = if(it.get("Foto") != null) it.get("Foto") as String else ""
                grupoCreado.Creador = if(it.get("Creador") != null) it.get("Creador") as DocumentReference else null
                grupoCreado.Miembros = if(it.get("Miembros") != null) it.get("Miembros") as ArrayList<DocumentReference> else arrayListOf()

                var arraymiembro = grupoCreado.Miembros

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

                    for (responseUsuario in responseUsuarios) {
                        var usuarioAux = UsuariosModel()

                        if (responseUsuario.id != DataManager.emailUsuario) {
                            usuarioAux.Email = responseUsuario.id
                            usuarioAux.Nombre = responseUsuario.data.get("Nombre") as String
                            usuarioAux.Nombre = responseUsuario.data.get("Nombre") as String
                            usuarioAux.Foto = responseUsuario.data.get("Foto") as String

                            usuarioAux.Ecriptado = if(responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false
                            usuarioAux.DesencriptarInfo()

                            usuariosAdapter.addItem(usuarioAux)

                        }
                    }



                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error al conseguir los usuarios del grupo", exception)
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

                            if (responseUsuario.id == DataManager.emailUsuario) {
                                DataManager.showToast(this, "El usuario creador ya es parte de los miembros")
                            }
                            else if (responseUsuario.data!!.get("Carrera") != null) {
                                var responseUsuarioCarrera = responseUsuario.data!!.get("Carrera") as DocumentReference
                                if (responseUsuarioCarrera.id != carreraUsuario!!.id) {
                                    DataManager.showToast(this,"El usuario pertenece a otra carrera"
                                    )
                                } else {
                                    usuarioAux.Email = responseUsuario.id
                                    usuarioAux.Nombre =
                                        responseUsuario.data!!.get("Nombre") as String
                                    usuarioAux.Foto = responseUsuario.data!!.get("Foto") as String

                                    usuariosAdapter.addItem(usuarioAux)

                                    this.editTextAddMember!!.setText("")
                                }

                            }
                            else{
                                DataManager.showToast(this, "Error al agregar usuario")
                            }



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

    private fun ingresarInfoGrupo() {
        grupoCreado.Nombre = editTextNombre?.text.toString()
        grupoCreado.Descripcion = editTextDescripcion?.text.toString()

        var listaMiembros : MutableList<UsuariosModel>? = arrayListOf()
        if (!usuariosAdapter.listaUsuariosGrupo.isEmpty())
            listaMiembros = usuariosAdapter.listaUsuariosGrupo


        //TODO: AGREGAR VALIDACION DE QUE LA LISTA DE USUARIOS DEBE SER MAYOR A 5
        if(     grupoCreado.Nombre.isEmpty()
            ||  grupoCreado.Descripcion.isEmpty()
            ||  listaMiembros!!.count() < 5
            ||  (grupoCreado.Foto.isEmpty() && filepath == null)
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


            if(grupoCreado.Foto.isEmpty() && filepath == null){
                DataManager.showToast(this, "Error, falta agregar una foto para el grupo")
            }

        }else{
            var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)
            grupoCreado.Creador = documentReferenceUserLogged
            grupoCreado.Miembros = arrayListOf()

            grupoCreado.Miembros.add(grupoCreado.Creador!!)

            for (miembroGrupo in listaMiembros!!){
                var documentReferenceMiembro = FirebaseFirestore.getInstance().collection("Usuarios").document(miembroGrupo.Email)
                grupoCreado.Miembros.add(documentReferenceMiembro)
            }

            DataManager.showProgressDialog("Creando grupo")

            if (grupoCreado.id.isEmpty())
                crearGrupo()
            else
                editarGrupo()

        }

    }


    private fun crearGrupo(){
        //TODO: AGREGAR CODIGO PARA INCREMENTAR EL NUMERO DE GRUPOS CREADOS EN EL USUARIO

        grupoCreado.Foto = UUID.randomUUID().toString() + ".jpg"
        var pathImage = "images/Grupos/${grupoCreado.Foto}"

        grupoCreado.id = FirebaseFirestore.getInstance().collection("Grupos").document().id

        FirebaseFirestore.getInstance().collection("Conversacion")
            .add(
                hashMapOf(
                    "Conversacion" to "Grupo"
                )
            ).addOnCompleteListener { responseChatCreation ->

                if (responseChatCreation.isSuccessful) {
                    grupoCreado.Conversacion =
                        FirebaseFirestore.getInstance().collection("Conversacion")
                            .document(responseChatCreation.result.id)

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
                        ).addOnCompleteListener { responseUserCreation ->
                            if (responseUserCreation.isSuccessful) {
                                subirFoto(pathImage)
                            } else {
                                DataManager.showToast(
                                    this,
                                    "Error: ${responseUserCreation.exception!!.message}"
                                )
                            }
                        }


                } else {
                    DataManager.showAlert(this, "No se pudo crear la conversación del Grupo")
                    Log.w(TAG, "Error al crear la conversación del Grupo", responseChatCreation.exception)
                }

            }
    }

    private fun editarGrupo() {

        FirebaseFirestore.getInstance().collection("Grupos").document(grupoCreado.id)
            .update(
                mapOf(
                    "Nombre" to grupoCreado.Nombre,
                    "Descripcion" to grupoCreado.Descripcion,
                    "Miembros" to grupoCreado.Miembros
                )
            ).addOnCompleteListener { responseUserCreation ->
                if (responseUserCreation.isSuccessful) {

                    if (subirFotoGrupo) {
                        var pathImage = "images/Grupos/${grupoCreado.Foto}"
                        subirFoto(pathImage)
                    } else {
                        salirAVentanaMain()
                    }
                } else {
                    DataManager.showToast(
                        this,
                        "Error: ${responseUserCreation.exception!!.message}"
                    )
                }
            }


    }
    

    private fun subirFoto(pathImage : String){
        var imageRef = FirebaseStorage.getInstance().reference.child(pathImage)

        imageRef.putFile(filepath!!)
            .addOnSuccessListener { responseImageUpload ->
                salirAVentanaMain()
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
                var progressPercent =
                    (100.0 * responseImageUpload.bytesTransferred) / responseImageUpload.totalByteCount
                DataManager.updateMessage("Uploaded ${progressPercent.toInt()}%")
            }
    }
    
    
    private fun subirFotoGrupo () {
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

            subirFotoGrupo = true
        }
    }

    private fun salirAVentanaMain(){
        DataManager.showToast(this, "Información subida correctamente")

        val vIntent = Intent(this, MainActivity::class.java)
        startActivity(vIntent)
    }

}
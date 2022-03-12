package com.example.purrrfectpoi

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.example.purrrfectpoi.Models.MedallasModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.adapters.MedallasAdapter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.psm.hiring.Utils.DataManager
import kotlinx.android.synthetic.main.item_group.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ProfileActivity : AppCompatActivity() {

    val TAG="ProfileActivity"

    var btn_Editar : Button? = null;
    var btn_Editar_Foto : ImageView? = null;
    var header_back : View? = null;
    var navbar_back : ImageView? = null;

    var image_Foto : ImageView? = null;
    var editTextNombre : EditText? = null;
    var editTextApPaterno : EditText? = null;
    var editTextApMaterno : EditText? = null;

    private lateinit var recyclerViewMedallas : RecyclerView
    private lateinit var rewardsAdapter: MedallasAdapter

    var usuarioProfile : UsuariosModel = UsuariosModel()
    var editable:Boolean = false

    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.btn_Editar = findViewById<Button>(R.id.profile_btn_editar)
        this.btn_Editar_Foto = findViewById<ImageView>(R.id.profile_image_btn_editar_foto)
        this.navbar_back = findViewById<ImageView>(R.id.profile_image_btn_back)
        this.header_back = findViewById<View>(R.id.profile_header_back)

        this.image_Foto = findViewById<ImageView>(R.id.profile_image_foto)
        this.editTextNombre = findViewById<EditText>(R.id.profile_input_nombre)
        this.editTextApPaterno = findViewById<EditText>(R.id.profile_input_ap_paterno)
        this.editTextApMaterno = findViewById<EditText>(R.id.profile_input_ap_materno)
        this.recyclerViewMedallas = findViewById<RecyclerView>(R.id.profile_rv_medallas)

        editTextNombre!!.isEnabled = false
        editTextApPaterno!!.isEnabled = false
        editTextApMaterno!!.isEnabled = false

        setUpInfoProfile()

        this.btn_Editar?.setOnClickListener {
            editarUsuario(this)
        }

        this.btn_Editar_Foto?.setOnClickListener {
            editarFotoUsuario(this)
        }

        this.navbar_back?.setOnClickListener {
            val vIntent = Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }

        this.header_back?.setOnClickListener {
            val vIntent = Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }


    }

    private fun setUpInfoProfile() {
        db = FirebaseFirestore.getInstance()
        db.collection("Usuarios").document(DataManager.emailUsuario!!).get()
            .addOnSuccessListener {

                usuarioProfile.Nombre =     if(it.get("Nombre") != null)    it.get("Nombre") as String else ""
                usuarioProfile.ApPaterno =  if(it.get("ApPaterno") != null) it.get("ApPaterno") as String else ""
                usuarioProfile.ApMaterno =  if(it.get("ApMaterno") != null) it.get("ApMaterno") as String else ""
                usuarioProfile.Medallas =   if(it.get("Medallas") != null)  it.get("Medallas") as ArrayList<DocumentReference> else arrayListOf()
                usuarioProfile.Foto =       if(it.get("Foto") != null)      it.get("Foto") as String else ""


                Log.w(TAG, "Medallas: ${usuarioProfile.Medallas}")

                editTextNombre?.setText(usuarioProfile.Nombre)
                editTextApPaterno?.setText(usuarioProfile.ApPaterno)
                editTextApMaterno?.setText(usuarioProfile.ApMaterno)

                setUpImageUser()

                setupRecyclerView()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    private fun editarUsuario(profileActivity: ProfileActivity) {
        if (!editable){
            editTextNombre!!.isEnabled = true
            editTextApPaterno!!.isEnabled = true
            editTextApMaterno!!.isEnabled = true

            editable = true
        }
        else{
            DataManager.progressDialog!!.setMessage("Ingresando")
            DataManager.progressDialog!!.setCancelable(false)
            DataManager.progressDialog!!.show()


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

                if(DataManager.progressDialog!!.isShowing) DataManager.progressDialog!!.dismiss()
            }else{

                db.collection("Usuarios").document(DataManager.emailUsuario!!)
                    .update(
                        mapOf(
                            "Nombre" to usuarioProfile.Nombre,
                            "ApPaterno" to usuarioProfile.ApPaterno,
                            "ApMaterno" to usuarioProfile.ApMaterno
                        )
                    ).addOnCompleteListener{
                        if (it.isSuccessful) {
                            editTextNombre!!.isEnabled = false
                            editTextApPaterno!!.isEnabled = false
                            editTextApMaterno!!.isEnabled = false

                            editable = false


                            Toast.makeText(applicationContext, "Se ha editado con exito la información", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            DataManager.showAlert(this, "Se ha producido un error al editar el usuario")
                        }
                    }


            }
        }
    }

    private fun editarFotoUsuario(profileActivity: ProfileActivity) {
        startFileChooser()
    }

    private fun startFileChooser () {
        var i = Intent ()
        i.setType ("image/*")
        i.setAction (Intent.ACTION_GET_CONTENT)
        startActivityForResult (Intent.createChooser (i, "Choose Picture"), 111)
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        lateinit var filepath : Uri

        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null)
        {
            filepath = data!!.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            image_Foto!!.setImageBitmap(bitmap)

            var pd = ProgressDialog ( this)
            pd.setTitle ("Uploading")
            pd.show ()


            var imageRef : StorageReference? = null
            var pathImage : String = ""

            var editarPathFotoUsuario = false
            if (usuarioProfile.Foto.isEmpty()) {
                usuarioProfile.Foto = UUID.randomUUID().toString() + ".jpg"
                editarPathFotoUsuario = true
            }

            pathImage = "images/Usuarios/${usuarioProfile.Foto}"


            imageRef = FirebaseStorage.getInstance().reference.child( pathImage)


            imageRef.putFile (filepath)
                .addOnSuccessListener { p0 ->
                    if (editarPathFotoUsuario){

                        db.collection("Usuarios").document(DataManager.emailUsuario!!)
                            .update(
                                mapOf(
                                    "Foto" to usuarioProfile.Foto
                                )
                            ).addOnCompleteListener{
                                if (it.isSuccessful) {
                                    pd.dismiss()
                                    Toast.makeText(applicationContext, "Foto del Usuario editada correctamente", Toast.LENGTH_LONG).show()
                                }
                                else{
                                    pd.dismiss()
                                    DataManager.showAlert(this, "Se ha producido un error al editar el usuario")
                                }
                            }


                    }
                    else{
                        pd.dismiss()
                        Toast.makeText(applicationContext, "Foto del Usuario editada correctamente", Toast.LENGTH_LONG).show()
                    }

                }
                .addOnFailureListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { p0 ->
                    var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Uploaded ${progress.toInt()}%")
                }

        }
    }

    private fun setUpImageUser(){
        if (usuarioProfile.Foto.isNotEmpty()) {
            /*
            val storageRef = FirebaseStorage.getInstance().reference.child("images/Usuarios/${usuarioProfile.Foto}")

            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile)
                .addOnSuccessListener {

                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    image_Foto!!.setImageBitmap(bitmap)

                }
                .addOnFailureListener {
                    Toast.makeText(this, "No pudo cargar la imagen del Usuario", Toast.LENGTH_SHORT).show()
                }
            */

            FirebaseStorage.getInstance().getReference("images/Usuarios/${usuarioProfile.Foto}").downloadUrl
                .addOnSuccessListener {

                    Glide.with(this)
                        .load(it.toString())
                        .into(image_Foto!!)
                }
        }
        else {
            image_Foto!!.setImageResource(R.drawable.logo_app)
        }
    }

    private fun setupRecyclerView(){
        db.collection("Medallas").get()
            .addOnSuccessListener { medallas ->

                var medallasParam : MutableList<MedallasModel> = mutableListOf()


                for (medalla in medallas) {
                    Log.d(TAG, "${medalla.id} => ${medalla.data}")

                    var medallaAux = MedallasModel()
                    medallaAux.Nombre = medalla.data.get("Nombre") as String
                    medallaAux.Foto = medalla.data.get("Foto") as String
                    medallaAux.Tipo = medalla.data.get("Tipo") as String
                    medallaAux.CantidadRequerida = (medalla.data.get("CantidadRequerida") as Long).toInt()
                    medallasParam.add(medallaAux)
                }

                //TODO: LA IDEA AQUI ES MANDAR medallas POR PARAMETRO, PERO NO SE PUEDE CASTEAR, POR LO QUE SE crea medallaAux


                rewardsAdapter = MedallasAdapter(medallasParam)
                recyclerViewMedallas.apply {
                    adapter = rewardsAdapter
                    layoutManager = LinearLayoutManager(context)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        /*
       if (!usuarioProfile.Medallas.isEmpty()) {

           var stringMedalla : MutableList<String> = mutableListOf()
           for (medalla in usuarioProfile.Medallas){
               Log.d(TAG, "Id: ${medalla.id}")
               Log.d(TAG, "Path: ${medalla.path}")

               stringMedalla.add(medalla.id)
           }




           db.collection("Medallas").whereIn(FieldPath.documentId(), stringMedalla).get()
               .addOnSuccessListener { medallas ->
                   for (medalla in medallas) {
                       Log.d(TAG, "${medalla.id} => ${medalla.data}")
                   }

                   //TODO: LA IDEA AQUI ES MANDAR medallas POR PARAMETRO
                   rewardsAdapter = RewardsAdapter(medallas as MutableList<Medallas>)
                   recyclerViewMedallas.apply {
                       adapter = rewardsAdapter
                       layoutManager = LinearLayoutManager(context)
                   }
               }
               .addOnFailureListener { exception ->
                   Log.w(TAG, "Error getting documents: ", exception)
               }

        }
        else{
            //MOSTRAR NOTA DE QUE NO HAY MEDALLAS
        }

        */
    }

}



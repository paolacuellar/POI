package com.example.purrrfectpoi

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import java.util.*
import kotlin.collections.ArrayList

class NewPostActivity : AppCompatActivity() {

    var btnRegresar : ImageView? = null;
    var fotoPerfil : ImageView? = null;
    var nombreUsuario : TextView? = null;

    var postText : EditText? = null;
    var postImage : ImageView? = null;
    var btnImage : ImageButton? = null;
    var btnPost : Button? = null;

    var idCarrera : String? = null;
    var filepath : Uri? = null;

    private lateinit var db : FirebaseFirestore
    private lateinit var documentReferenceUserLogged : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        this.btnRegresar = findViewById<ImageView>(R.id.btnRegresarNewPost)
        this.fotoPerfil = findViewById<ImageView>(R.id.chatUserImage)
        this.nombreUsuario = findViewById<TextView>(R.id.chatNameText)
        this.postText = findViewById<EditText>(R.id.messageTextField)
        this.postImage = findViewById<ImageView>(R.id.cover)
        this.btnImage = findViewById<ImageButton>(R.id.newChatButton)
        this.btnPost = findViewById<Button>(R.id.btnPost)

        val bundle : Bundle?= intent.extras
        idCarrera = bundle!!.getString("IdCarrera")

        documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)

        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }

        this.btnImage?.setOnClickListener {
            uploadPhoto()
        }

        this.btnPost?.setOnClickListener {
            sendPost()
        }

        setUpInfoCreator()

    }

    private fun setUpInfoCreator() {
        db = FirebaseFirestore.getInstance()
        db.collection("Usuarios").document(DataManager.emailUsuario!!).get()
            .addOnSuccessListener { responseUsuario ->

                var fotoAux : String = ""
                fotoAux = responseUsuario.get("Foto") as String
                if (fotoAux.isNotEmpty()) {
                    FirebaseStorage.getInstance().getReference("images/Usuarios/${fotoAux}").downloadUrl
                        .addOnSuccessListener {
                            Glide.with(this)
                                .load(it.toString())
                                .into(fotoPerfil!!)
                        }
                }
                else {
                    fotoPerfil!!.setImageResource(R.drawable.foto_default_perfil)
                }

                var username : String = ""
                if (responseUsuario.get("Nombre") != null) {
                    username = responseUsuario.get("Nombre") as String
                }
                if(responseUsuario.get("ApPaterno") != null) {
                    username += " " + responseUsuario.get("ApPaterno") as String
                }
                nombreUsuario?.text = username

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    private fun uploadPhoto() {
        var i = Intent ()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data!!.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            postImage!!.setImageBitmap(bitmap)
        }

    }

    private fun sendPost() {
        var texto : String = postText?.text.toString()
        if (texto.isEmpty()) {
            postText?.setError("Texto vacío")
        }
        else {
            var arrayComent : ArrayList<DocumentReference> = arrayListOf()

            db = FirebaseFirestore.getInstance()
            db.collection("Publicaciones")
                .add(
                    hashMapOf(
                        "Texto" to texto,
                        "Foto" to "",
                        "Creador" to documentReferenceUserLogged,
                        "FechaCreacion" to FieldValue.serverTimestamp(),
                        "Editado" to false,
                        "Latitud" to "",
                        "Longitud" to "",
                        "Comentarios" to arrayComent
                    )
                ).addOnSuccessListener { responsePost ->

                    DataManager.updateBadges("Posts")

                    if (filepath != null) {
                        var strPhoto = UUID.randomUUID().toString() + ".jpg"
                        var pathImage = "images/Publicaciones/${strPhoto}"
                        var imageRef = FirebaseStorage.getInstance().reference.child(pathImage)
                        imageRef.putFile(filepath!!)
                            .addOnSuccessListener { responseImageUpload ->

                                db.collection("Publicaciones").document(responsePost.id)
                                    .update(
                                        "Foto", strPhoto
                                    )
                                    .addOnSuccessListener {

                                        db.collection("Carrera").document(idCarrera!!)
                                            .update(
                                                "Publicaciones", FieldValue.arrayUnion(responsePost)
                                            )

                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)

                                    }

                            }
                    }
                    else {
                        db.collection("Carrera").document(idCarrera!!)
                            .update(
                                "Publicaciones", FieldValue.arrayUnion(responsePost)
                            )

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
        }

    }

}
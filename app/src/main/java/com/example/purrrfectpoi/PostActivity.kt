package com.example.purrrfectpoi

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.ComentariosModel
import com.example.purrrfectpoi.adapters.ComentariosAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import org.w3c.dom.Text

class PostActivity : AppCompatActivity() {

    private lateinit var recyclerViewCom : RecyclerView
    private lateinit var comentariosAdapter: ComentariosAdapter

    var btnRegresar : ImageView? = null;
    var fotoPerfil : ImageView? = null;
    var nombreUsuario : TextView? = null;

    var postText : TextView? = null;
    var postImage : ImageView? = null;

    var comText : EditText? = null;
    var comSend : ImageView? = null;

    var idPost : String? = null;
    private lateinit var db : FirebaseFirestore
    private lateinit var documentReferenceUserLogged : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        this.recyclerViewCom = findViewById<RecyclerView>(R.id.listComRecyclerView)

        this.btnRegresar = findViewById<ImageView>(R.id.btnRegresarPost)
        this.fotoPerfil = findViewById<ImageView>(R.id.chatUserImage)
        this.nombreUsuario = findViewById<TextView>(R.id.chatNameText)
        this.postText = findViewById<TextView>(R.id.textView)
        // FALTA ASIGNAR postImage PORQUE NO ESTÁ EL IMAGEVIEW EN EL XML
        this.comText = findViewById<EditText>(R.id.messageTextField)
        this.comSend = findViewById<ImageView>(R.id.sendMessageButton)

        val bundle : Bundle?= intent.extras
        idPost = bundle!!.getString("IdPost")

        documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)

        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }

        this.comSend?.setOnClickListener {
            sendComment()
        }

        setUpInfoPost()
        setUpRecyclerView()

    }

    private fun setUpInfoPost() {
        db = FirebaseFirestore.getInstance()
        db.collection("Publicaciones").document(idPost!!).get()
            .addOnSuccessListener { responsePost ->

                postText?.text = if(responsePost.get("Texto") != null)    responsePost.get("Texto") as String else ""
                // AQUI SE PONDRÁ LA IMAGEN SI ES QUE HAY UNA IMAGEN EN EL POST

                var documentReferenceCreator = responsePost.get("Creador") as DocumentReference

                db.collection("Usuarios").document(documentReferenceCreator.id).get()
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
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al conseguir la información del Post", exception)
            }

    }

    private fun setUpRecyclerView() {
        db = FirebaseFirestore.getInstance()
        db.collection("Publicaciones").document(idPost!!).get()
            .addOnSuccessListener { responsePost ->

                var comParam : MutableList<ComentariosModel> = mutableListOf()
                comentariosAdapter = ComentariosAdapter(comParam)
                recyclerViewCom.apply {
                    adapter = comentariosAdapter
                    layoutManager = LinearLayoutManager(context)
                }

                var coms = responsePost.get("Comentarios") as ArrayList<DocumentReference>
                for (com in coms) {

                    db.collection("Comentarios").document(com.id)
                        .get()
                        .addOnSuccessListener { responseCom ->

                            var comAux = ComentariosModel()
                            comAux.id = responseCom.id
                            comAux.Texto = if(responseCom.get("Texto") != null)    responseCom.get("Texto") as String else ""
                            comAux.Creador = responseCom.get("Creador") as DocumentReference
                            comAux.FechaCreacion = responseCom.get("FechaCreacion") as Timestamp
                            comAux.Editado = responseCom.get("Editado") as Boolean

                            comParam.add(comAux)
                            comentariosAdapter.addItem(comAux)

                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error consiguiendo el Comentario", exception)
                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al conseguir la información del Post", exception)
            }
    }

    private fun sendComment() {
        var texto : String = comText?.text.toString()
        if (texto.isEmpty()) {
            comText?.setError("Texto vacío")
        }
        else {
            comText?.setText("")

            db = FirebaseFirestore.getInstance()
            db.collection("Comentarios")
                .add(
                    hashMapOf(
                        "Texto" to texto,
                        "Creador" to documentReferenceUserLogged,
                        "FechaCreacion" to FieldValue.serverTimestamp(),
                        "Editado" to false
                    )
                ).addOnSuccessListener { responseCom ->

                    db.collection("Publicaciones").document(idPost!!)
                        .update(
                            "Comentarios", FieldValue.arrayUnion(responseCom)
                        )

                    db.collection("Comentarios").document(responseCom.id)
                        .get()
                        .addOnSuccessListener { responseCC ->

                            var comAux = ComentariosModel()
                            comAux.id = responseCC.id
                            comAux.Texto = responseCC.get("Texto") as String
                            comAux.Creador = responseCC.get("Creador") as DocumentReference
                            comAux.FechaCreacion = responseCC.get("FechaCreacion") as Timestamp
                            comAux.Editado = responseCC.get("Editado") as Boolean

                            comentariosAdapter.addItem(comAux)

                        }

                }
        }
    }

}
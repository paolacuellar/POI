package com.example.purrrfectpoi

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.ComentariosModel
import com.example.purrrfectpoi.Models.UsuariosModel
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
    private lateinit var builder : AlertDialog.Builder

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
        this.postImage = findViewById<ImageView>(R.id.cover)
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
                var fotoPost : String = ""
                fotoPost = responsePost.get("Foto") as String
                if (fotoPost.isNotEmpty()) {
                    FirebaseStorage.getInstance().getReference("images/Publicaciones/${fotoPost}").downloadUrl
                        .addOnSuccessListener {
                            Glide.with(this)
                                .load(it.toString())
                                .into(postImage!!)
                        }
                }
                else {
                    postImage!!.visibility = View.GONE
                }

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

                        var userAux = UsuariosModel()
                        userAux.Nombre = if(responseUsuario.get("Nombre") != null)    responseUsuario.get("Nombre") as String else ""
                        userAux.ApPaterno =  if(responseUsuario.get("ApPaterno") != null) responseUsuario.get("ApPaterno") as String else ""
                        userAux.Ecriptado = if(responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false
                        userAux.DesencriptarInfo()
        
                        var username = userAux.Nombre + " " + userAux.ApPaterno
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
                comentariosAdapter.setOnItemClickListener(object : ComentariosAdapter.onItemClickListener{
                    override fun onItemClick(view: View, position: Int) {
                        builder = AlertDialog.Builder(this@PostActivity)
                        builder.setTitle("Eliminar comentario")
                        builder.setMessage("Esta acción no se puede deshacer")
                        builder.setPositiveButton("Confirmar") { dialogInterface, which ->
                            val docRefCom = db.collection("Comentarios").document(comParam[position].id)
                            db.collection("Publicaciones").document(idPost!!)
                                .update("Comentarios", FieldValue.arrayRemove(docRefCom))
                            db.collection("Comentarios").document(comParam[position].id).delete()
                            comentariosAdapter.deleteItem(comParam[position].id)
                        }
                        builder.setNegativeButton("Cancelar") { dialogInterface, which -> }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.show()
                    }
                })

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
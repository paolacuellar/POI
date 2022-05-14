package com.example.purrrfectpoi.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.AddGroupActivity
import com.example.purrrfectpoi.Models.PublicacionesModel
import com.example.purrrfectpoi.NewPostActivity
import com.example.purrrfectpoi.PostActivity
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.MuroGeneralAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.psm.hiring.Utils.DataManager

class MuroFragment: Fragment() {

    private lateinit var recyclerViewMuro : RecyclerView
    private lateinit var muroGeneralAdapter : MuroGeneralAdapter
    private lateinit var buttonCrearPost : FloatingActionButton

    var idCarrera : String = ""

    var txtTituloPantalla : TextView? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_muro,container,false)
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.txtTituloPantalla = requireActivity().findViewById<TextView>(R.id.main_text_title)
        this.txtTituloPantalla!!.text = "Muro General";

        this.buttonCrearPost = requireActivity().findViewById<FloatingActionButton>(R.id.menu_btn_floating)
        this.buttonCrearPost.visibility = View.VISIBLE
        //this.buttonCrearPost.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_write_brown));
        //this.buttonCrearPost.setBackgroundResource(R.drawable.ic_write_brown);
        this.buttonCrearPost.setImageResource(R.drawable.ic_write_brown);

        buttonCrearPost.setOnClickListener{
            val intent = Intent(activity, NewPostActivity::class.java)
            intent.putExtra("IdCarrera", idCarrera)
            startActivity(intent)
        }

        this.recyclerViewMuro = view.findViewById<RecyclerView>(R.id.listPostsRecyclerView)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        if (DataManager.emailUsuario != null) {
            FirebaseFirestore.getInstance().collection("Usuarios")
                .document(DataManager.emailUsuario!!)
                .get()
                .addOnSuccessListener { responseUsuario ->

                    var postsParam: MutableList<PublicacionesModel> = mutableListOf()
                    muroGeneralAdapter = MuroGeneralAdapter(postsParam)
                    recyclerViewMuro.apply {
                        adapter = muroGeneralAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                    muroGeneralAdapter.setOnItemClickListener(object :
                        MuroGeneralAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(activity, PostActivity::class.java)
                            intent.putExtra("IdPost", postsParam[position].id)
                            startActivity(intent)

                        }
                    })

                    var documentReferenceCarrera =
                        responseUsuario.get("Carrera") as DocumentReference

                    FirebaseFirestore.getInstance().collection("Carrera")
                        .document(documentReferenceCarrera.id)
                        .get()
                        .addOnSuccessListener { responseCarrera ->

                            idCarrera = responseCarrera.id
                            var posts =
                                responseCarrera.get("Publicaciones") as ArrayList<DocumentReference>

                            for (post in posts) {

                                FirebaseFirestore.getInstance().collection("Publicaciones")
                                    .document(post.id)
                                    .get()
                                    .addOnSuccessListener { responsePost ->

                                        var postAux = PublicacionesModel()
                                        postAux.id = responsePost.id
                                        postAux.Texto =
                                            if (responsePost.get("Texto") != null) responsePost.get(
                                                "Texto"
                                            ) as String else ""
                                        postAux.Foto =
                                            if (responsePost.get("Foto") != null) responsePost.get("Foto") as String else ""
                                        postAux.Creador =
                                            responsePost.get("Creador") as DocumentReference
                                        postAux.FechaCreacion =
                                            responsePost.get("FechaCreacion") as Timestamp
                                        postAux.Editado = responsePost.get("Editado") as Boolean
                                        postAux.Latitud =
                                            if (responsePost.get("Latitud") != null) responsePost.get(
                                                "Latitud"
                                            ) as String else ""
                                        postAux.Longitud =
                                            if (responsePost.get("Longitud") != null) responsePost.get(
                                                "Longitud"
                                            ) as String else ""

                                        postsParam.add(postAux)
                                        muroGeneralAdapter.addItem(postAux)

                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(
                                            ContentValues.TAG,
                                            "Error consiguiendo la Publicacion",
                                            exception
                                        )
                                    }

                            }

                        }
                        .addOnFailureListener { exception ->
                            Log.w(
                                ContentValues.TAG,
                                "Error consiguiendo la Carrera del Usuario",
                                exception
                            )
                        }

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error consiguiendo el Usuario", exception)
                }

        }

    }

}
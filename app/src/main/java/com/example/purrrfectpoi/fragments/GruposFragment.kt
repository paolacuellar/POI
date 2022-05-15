package com.example.purrrfectpoi.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.AddGroupActivity
import com.example.purrrfectpoi.GroupActivity
import com.example.purrrfectpoi.Models.GruposModel
import com.example.purrrfectpoi.ProfileActivity
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.GruposAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager

class GruposFragment: Fragment() {

    private lateinit var recyclerViewGrupos : RecyclerView
    private lateinit var gruposAdapter: GruposAdapter

    var txtTituloPantalla : TextView? = null;
    private lateinit var buttonCrearGrupo : FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grupos,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.recyclerViewGrupos = view.findViewById<RecyclerView>(R.id.listGroupsRecyclerView)

        this.txtTituloPantalla = requireActivity().findViewById<TextView>(R.id.main_text_title)
        this.txtTituloPantalla!!.text = "Mis Grupos";

        this.buttonCrearGrupo = requireActivity().findViewById<FloatingActionButton>(R.id.menu_btn_floating)
        this.buttonCrearGrupo.visibility = View.VISIBLE
        //this.buttonCrearGrupo.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_group_add_brown));
        this.buttonCrearGrupo.setImageResource(R.drawable.ic_group_add_brown);

        buttonCrearGrupo.setOnClickListener{
            val intent = Intent(activity, AddGroupActivity::class.java)
            startActivity(intent)
        }


        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        if (DataManager.emailUsuario != null) {
            var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios")
                .document(DataManager.emailUsuario!!)

            FirebaseFirestore.getInstance().collection("Grupos")
                .whereArrayContains("Miembros", documentReferenceUserLogged)
                .get()
                .addOnSuccessListener { responseGrupos ->

                    var gruposParam: MutableList<GruposModel> = mutableListOf()

                    for (responseGrupo in responseGrupos) {
                        var grupoAux = GruposModel()
                        grupoAux.id = responseGrupo.id
                        grupoAux.Nombre = responseGrupo.data.get("Nombre") as String
                        grupoAux.Foto = responseGrupo.data.get("Foto") as String
                        grupoAux.Creador = if (responseGrupo.get("Creador") != null) responseGrupo.get("Creador") as DocumentReference else null
                        //grupoAux.Conversacion =  if(responseGrupo.data.get("Conversacion") != null)    responseGrupo.data.get("Conversacion") as DocumentReference else null
                        gruposParam.add(grupoAux)
                    }

                    gruposAdapter = GruposAdapter(gruposParam)
                    recyclerViewGrupos.apply {
                        adapter = gruposAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error consiguiendo los Grupos", exception)
                }
        }
    }
}
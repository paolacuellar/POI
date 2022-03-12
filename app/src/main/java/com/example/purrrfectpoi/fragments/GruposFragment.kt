package com.example.purrrfectpoi.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.GruposModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.GruposAdapter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager

class GruposFragment: Fragment() {

    private lateinit var recyclerViewGrupos : RecyclerView
    private lateinit var gruposAdapter: GruposAdapter
    
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

        var gruposCarrera = GruposModel();

        var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)

        FirebaseFirestore.getInstance().collection("Grupos")
            .whereArrayContains("Miembros", documentReferenceUserLogged)
            .get()
            .addOnSuccessListener { grupos ->

                var gruposParam : MutableList<GruposModel> = mutableListOf()
                
                for (grupo in grupos) {
                    Log.d(TAG, "${grupo.id} => ${grupo.data}")

                    var grupoAux = GruposModel()
                    grupoAux.Nombre = grupo.data.get("Nombre") as String
                    grupoAux.Foto = grupo.data.get("Foto") as String
                    gruposParam.add(grupoAux)
                }

                //TODO: LA IDEA AQUI ES MANDAR grupos POR PARAMETRO, PERO NO SE PUEDE CASTEAR, POR LO QUE SE crea grupoAux


                gruposAdapter = GruposAdapter(gruposParam)
                recyclerViewGrupos.apply {
                    adapter = gruposAdapter
                    layoutManager = LinearLayoutManager(context)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}
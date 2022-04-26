package com.example.purrrfectpoi.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.ChatActivity
import com.example.purrrfectpoi.Models.ConversacionesModel
import com.example.purrrfectpoi.Models.GruposModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.GruposAdapter
import com.example.purrrfectpoi.adapters.UsuariosChatsAdapter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager

class GroupMembersFragment:Fragment() {

    private lateinit var recyclerViewmiembrosGrupo : RecyclerView
    private lateinit var miembrosGrupoAdapter: UsuariosChatsAdapter

    var idGrupo : String? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_group_members,container,false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idGrupo = arguments?.getString("grupoId")

        this.recyclerViewmiembrosGrupo = view.findViewById<RecyclerView>(R.id.listMembersRecyclerView)

        miembrosGrupoAdapter = UsuariosChatsAdapter(arrayListOf(), false)
        recyclerViewmiembrosGrupo.apply {
            adapter = miembrosGrupoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        miembrosGrupoAdapter.setOnItemClickListener(object : UsuariosChatsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                /*
                val intent = Intent(activity, ChatActivity::class.java)
                intent.putExtra("Email", miembrosGrupoAdapter.userChats[position].Email)
                intent.putExtra("IdChat", miembrosGrupoAdapter.userChats[position].)
                startActivity(intent)
                */
            }
        })

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        FirebaseFirestore.getInstance().collection("Grupos").document(idGrupo!!)
            .get()
            .addOnSuccessListener { responseGrupo ->

                var arrayMiembrosGrupo = if(responseGrupo.get("Miembros") != null) responseGrupo.get("Miembros") as ArrayList<DocumentReference> else null

                //TODO: PARECE QUE EL "whereIn" NOMAS JALA CON 10 USUARIOS, SI ES ASÍ DEBERE HACER UN CICLO FOR DE CONSULTAS, O DEBERE MANEJARLO DE OTRA MANERA?
                FirebaseFirestore.getInstance().collection("Usuarios")
                    .whereIn(FieldPath.documentId(), arrayMiembrosGrupo!!)
                    .get()
                    .addOnSuccessListener { responseUsuarios ->
                        for (responseUsuario in responseUsuarios!!) {
                            var usuarioProfile = UsuariosModel()

                            usuarioProfile.Email = responseUsuario.id
                            usuarioProfile.Nombre = if (responseUsuario.get("Nombre") != null) responseUsuario.get("Nombre") as String else ""
                            usuarioProfile.ApPaterno = if (responseUsuario.get("ApPaterno") != null) responseUsuario.get("ApPaterno") as String else ""
                            usuarioProfile.ApMaterno = if (responseUsuario.get("ApMaterno") != null) responseUsuario.get("ApMaterno") as String else ""
                            usuarioProfile.Foto = if (responseUsuario.get("Foto") != null) responseUsuario.get("Foto") as String else ""

                            miembrosGrupoAdapter.addItem(usuarioProfile)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error al conseguir la información del Usuario", exception)
                    }



                /*
                for (miembroGrupo in arrayMiembrosGrupo!!) {
                    FirebaseFirestore.getInstance().collection("Usuarios").document(miembroGrupo.id).get()
                        .addOnSuccessListener { responseUsuario ->

                            var usuarioProfile = UsuariosModel()

                            usuarioProfile.Email = responseUsuario.id
                            usuarioProfile.Nombre =     if(responseUsuario.get("Nombre") != null)    responseUsuario.get("Nombre") as String else ""
                            usuarioProfile.ApPaterno =  if(responseUsuario.get("ApPaterno") != null) responseUsuario.get("ApPaterno") as String else ""
                            usuarioProfile.ApMaterno =  if(responseUsuario.get("ApMaterno") != null) responseUsuario.get("ApMaterno") as String else ""
                            usuarioProfile.Foto =       if(responseUsuario.get("Foto") != null)      responseUsuario.get("Foto") as String else ""

                            miembrosGrupoAdapter.addItem(usuarioProfile)
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error al conseguir la información del Usuario", exception)
                        }
                }
                */
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo los Grupos", exception)
            }
    }
}
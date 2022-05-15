package com.example.purrrfectpoi.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.TareasModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.R
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


                if (arrayMiembrosGrupo != null) {
                    for (miembro in arrayMiembrosGrupo) {

                        FirebaseFirestore.getInstance().collection("Usuarios")
                            .document(miembro.id)
                            .get()
                            .addOnSuccessListener { responseUsuario ->

                                var usuarioAux = UsuariosModel()

                                if (responseUsuario.id != DataManager.emailUsuario) {
                                    usuarioAux.Email = responseUsuario.id
                                    usuarioAux.Nombre = responseUsuario.data?.get("Nombre") as String
                                    usuarioAux.Nombre = responseUsuario.data?.get("Nombre") as String
                                    usuarioAux.Foto = responseUsuario.data?.get("Foto") as String

                                    usuarioAux.Ecriptado = if(responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false
                                    usuarioAux.DesencriptarInfo()

                                    miembrosGrupoAdapter.addItem(usuarioAux)

                                }

                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error al conseguir la información del Usuario", exception)
                            }

                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo los Grupos", exception)
            }
    }
}
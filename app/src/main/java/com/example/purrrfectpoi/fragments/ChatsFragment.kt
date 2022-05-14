package com.example.purrrfectpoi.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.ChatActivity
import com.example.purrrfectpoi.Models.ConversacionesModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.UsuariosChatsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager
import kotlin.collections.ArrayList

class ChatsFragment: Fragment() {

    private lateinit var recyclerViewChats : RecyclerView
    private lateinit var chatsUsuariosAdapter : UsuariosChatsAdapter
    private lateinit var buttonFlotanteMain : FloatingActionButton

    var txtTituloPantalla : TextView? = null;
    var btnBuscarChat : ImageButton? = null;
    var txtBuscarChat : EditText? = null;
    var buscarChat = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chats,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.btnBuscarChat = getView()?.findViewById<ImageButton>(R.id.newChatButton)
        this.txtBuscarChat = getView()?.findViewById<EditText>(R.id.newChatText)

        this.txtTituloPantalla = requireActivity().findViewById<TextView>(R.id.main_text_title)
        this.txtTituloPantalla!!.text = "Mis Chats";

        this.buttonFlotanteMain = requireActivity().findViewById<FloatingActionButton>(R.id.menu_btn_floating)
        this.buttonFlotanteMain.visibility = View.GONE


        this.btnBuscarChat?.setOnClickListener {
            buscarChats(txtBuscarChat?.text.toString())
            buscarChat = true
        }


        this.txtBuscarChat?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) { }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty() && buscarChat == true) {
                    setUpRecyclerView()

                    buscarChat = false
                }
            }

        })

        this.recyclerViewChats = view.findViewById<RecyclerView>(R.id.listChatsRecyclerView)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(
            DataManager.emailUsuario!!)

        FirebaseFirestore.getInstance().collection("Conversacion")
            .whereArrayContains("Participantes", documentReferenceUserLogged)
            .get()
            .addOnSuccessListener { responseChats ->

                //var chatsParam : MutableList<ConversacionesModel> = mutableListOf()
                var usersParam : MutableList<UsuariosModel> = mutableListOf()
                var arrayChats : MutableList<String> = mutableListOf()
                chatsUsuariosAdapter = UsuariosChatsAdapter(usersParam)
                recyclerViewChats.apply {
                    adapter = chatsUsuariosAdapter
                    layoutManager = LinearLayoutManager(context)
                }
                chatsUsuariosAdapter.setOnItemClickListener(object : UsuariosChatsAdapter.onItemClickListener{
                    override fun onItemClick(position: Int) {

                        //var userMail : String = usersParam[position].Email
                        //Toast.makeText(activity, "You clicked on user: $userMail", Toast.LENGTH_SHORT).show()

                        val intent = Intent(activity, ChatActivity::class.java)
                        intent.putExtra("Email", usersParam[position].Email)
                        intent.putExtra("IdChat", arrayChats[position])
                        startActivity(intent)
                    }
                })

                for (responseChat in responseChats) {
                    var chatAux = ConversacionesModel()
                    chatAux.id = responseChat.id
                    chatAux.Participantes = responseChat.data.get("Participantes") as ArrayList<DocumentReference>

                    for (p in chatAux.Participantes) {

                        if (p.id != documentReferenceUserLogged.id) {

                            FirebaseFirestore.getInstance().collection("Usuarios")
                                .document(p.id)
                                .get()
                                .addOnSuccessListener { responseUsuario ->

                                    var userAux = UsuariosModel()
                                    userAux.Email = responseUsuario.id
                                    userAux.Nombre = if(responseUsuario.get("Nombre") != null)    responseUsuario.get("Nombre") as String else ""
                                    userAux.ApPaterno =  if(responseUsuario.get("ApPaterno") != null) responseUsuario.get("ApPaterno") as String else ""
                                    userAux.ApMaterno =  if(responseUsuario.get("ApMaterno") != null) responseUsuario.get("ApMaterno") as String else ""
                                    userAux.Foto = if(responseUsuario.get("Foto") != null)      responseUsuario.get("Foto") as String else ""
                                    userAux.Conectado = responseUsuario.get("Conectado") as Boolean

                                    userAux.Ecriptado = if(responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false

                                    userAux.DesencriptarInfo()

                                    usersParam.add(userAux)
                                    arrayChats.add(chatAux.id)
                                    chatsUsuariosAdapter.addItem(userAux)
                                }
                                .addOnFailureListener { exception ->
                                    Log.w(ContentValues.TAG, "Error consiguiendo los Usuarios", exception)
                                }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo los Chats", exception)
            }
    }

    private fun buscarChats(busqueda : String) {
        if (busqueda.isEmpty()) {
            setUpRecyclerView()

        } else {
            var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(
                DataManager.emailUsuario!!)
            var existe = false

            FirebaseFirestore.getInstance().collection("Usuarios")
                .get()
                .addOnSuccessListener { responseUsuarios ->

                    var usersParam: MutableList<UsuariosModel> = mutableListOf()
                    var arrayChats : MutableList<String> = mutableListOf()
                    chatsUsuariosAdapter = UsuariosChatsAdapter(usersParam)
                    recyclerViewChats.apply {
                        adapter = chatsUsuariosAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                    chatsUsuariosAdapter.setOnItemClickListener(object : UsuariosChatsAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            var userMail : String = usersParam[position].Email
                            //Toast.makeText(activity, "User: $userMail Chat: $chatId", Toast.LENGTH_SHORT).show()

                            if (arrayChats[position] == "nuevo") {
                                var documentReferenceOtherUser = FirebaseFirestore.getInstance().collection("Usuarios").document(userMail)

                                FirebaseFirestore.getInstance().collection("Conversacion")
                                    .add(
                                        hashMapOf(
                                            "Participantes" to arrayListOf(documentReferenceUserLogged, documentReferenceOtherUser)
                                        )
                                    ).addOnCompleteListener { responseChatCreation ->

                                        if (responseChatCreation.isSuccessful) {
                                            arrayChats[position] = responseChatCreation.result.id

                                            val intent = Intent(activity, ChatActivity::class.java)
                                            intent.putExtra("Email", userMail)
                                            intent.putExtra("IdChat", arrayChats[position])
                                            startActivity(intent)

                                        } else {
                                            Toast.makeText(activity, "Error: ${responseChatCreation.exception!!.message}", Toast.LENGTH_SHORT).show()
                                        }

                                    }

                            } else {

                                val intent = Intent(activity, ChatActivity::class.java)
                                intent.putExtra("Email", userMail)
                                intent.putExtra("IdChat", arrayChats[position])
                                startActivity(intent)

                            }

                        }
                    })

                    for (responseUsuario in responseUsuarios) {

                        if (responseUsuario.id != documentReferenceUserLogged.id) {

                            FirebaseFirestore.getInstance().collection("Usuarios")
                                .document(responseUsuario.id)
                                .get()
                                .addOnSuccessListener { responseUser ->

                                    var userAux = UsuariosModel()
                                    userAux.Email = responseUser.id
                                    userAux.Nombre = if (responseUser.get("Nombre") != null) responseUser.get("Nombre") as String else ""
                                    userAux.ApPaterno = if (responseUser.get("ApPaterno") != null) responseUser.get("ApPaterno") as String else ""
                                    userAux.ApMaterno = if (responseUser.get("ApMaterno") != null) responseUser.get("ApMaterno") as String else ""
                                    userAux.Foto = if (responseUser.get("Foto") != null) responseUser.get("Foto") as String else ""
                                    userAux.Ecriptado = if(responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false
                                    userAux.DesencriptarInfo()

                                    if (userAux.Nombre.lowercase().contains(busqueda.lowercase()) ||
                                        userAux.ApPaterno.lowercase().contains(busqueda.lowercase())) {

                                        FirebaseFirestore.getInstance().collection("Conversacion")
                                            .whereArrayContains("Participantes", documentReferenceUserLogged)
                                            .get()
                                            .addOnSuccessListener { responseChats ->

                                                for (responseChat in responseChats) {
                                                    var chatAux = ConversacionesModel()
                                                    chatAux.id = responseChat.id
                                                    chatAux.Participantes = responseChat.data.get("Participantes") as ArrayList<DocumentReference>

                                                    for (p in chatAux.Participantes) {

                                                        if (p.id == userAux.Email) {
                                                            arrayChats.add(chatAux.id)
                                                            existe = true

                                                        }

                                                    }

                                                }

                                                if (!existe) {
                                                    arrayChats.add("nuevo")

                                                }

                                                existe = false
                                                usersParam.add(userAux)
                                                chatsUsuariosAdapter.addItem(userAux)

                                            }
                                            .addOnFailureListener { exception ->
                                                Log.w(ContentValues.TAG, "Error consiguiendo los Chats", exception)
                                            }

                                    }

                                }
                                .addOnFailureListener { exception ->
                                    Log.w(ContentValues.TAG, "Error consiguiendo el Usuario", exception)
                                }

                        }

                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error consiguiendo los Usuarios", exception)
                }

        }

    }


}
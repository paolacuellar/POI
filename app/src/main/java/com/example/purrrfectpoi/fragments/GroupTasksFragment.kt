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
import com.example.purrrfectpoi.AddTaskGroupActivity
import com.example.purrrfectpoi.Models.TareasModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.GroupTasksAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager

class GroupTasksFragment:Fragment() {

    private lateinit var recyclerViewTasksGrupo : RecyclerView
    private lateinit var tasksGrupoAdapter: GroupTasksAdapter

    private var isAuthor: Boolean = false

    private lateinit var buttonAddTask: FloatingActionButton

    var grupoId : String? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_group_tasks,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grupoId = arguments?.getString("grupoId")

        this.recyclerViewTasksGrupo = view.findViewById<RecyclerView>(R.id.listTasksRecyclerView)

        this.buttonAddTask = view.findViewById<FloatingActionButton>(R.id.tasks_btn_floating_add_task)

        buttonAddTask.setOnClickListener{
            val intent = Intent(view.context, AddTaskGroupActivity::class.java)
            intent.putExtra("grupoId", grupoId)
            view.context?.startActivity(intent)
        }

        tasksGrupoAdapter = GroupTasksAdapter(arrayListOf(), grupoId!!)
        recyclerViewTasksGrupo.apply {
            adapter = tasksGrupoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        tasksGrupoAdapter.setOnItemClickListener(object : GroupTasksAdapter.onItemClickListener{
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
        FirebaseFirestore.getInstance().collection("Grupos").document(grupoId!!)
            .get()
            .addOnSuccessListener { responseGrupo ->

                var authorGroup = if(responseGrupo.get("Creador") != null) responseGrupo.get("Creador") as DocumentReference else null

                setViews(authorGroup?.id.toString() == DataManager.emailUsuario)

                FirebaseFirestore.getInstance().collection("Grupos").document(grupoId!!).collection("Tareas")
                    .get()
                    .addOnSuccessListener { responseTareas ->

                        var arrayTareasModel = arrayListOf<TareasModel>()
                        for (responseTarea in responseTareas!!) {
                            var tareaEncargada = TareasModel()
                            tareaEncargada.id = responseTarea.id
                            tareaEncargada.Nombre = if (responseTarea.get("Nombre") != null) responseTarea.get("Nombre") as String else ""
                            tareaEncargada.FechaProgramada = if (responseTarea.get("FechaProgramada") != null) responseTarea.get("FechaProgramada") as Timestamp else null

                            arrayTareasModel.add(tareaEncargada)
                        }

                        arrayTareasModel.sortBy { it.FechaProgramada }
                        tasksGrupoAdapter.setListTareas(arrayTareasModel)

                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error al conseguir las Tareas del Grupo", exception)
                    }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo los Grupos", exception)
            }
    }



    private fun setViews(isAuthor: Boolean) {
        if (isAuthor) {
            this.buttonAddTask.visibility = View.VISIBLE
        }
        else{
            this.buttonAddTask.visibility = View.GONE
        }

        this.tasksGrupoAdapter.isAuthor = isAuthor
    }
}
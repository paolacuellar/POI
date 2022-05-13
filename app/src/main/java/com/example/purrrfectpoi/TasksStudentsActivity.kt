package com.example.purrrfectpoi

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.TrabajosModel
import com.example.purrrfectpoi.adapters.TasksStudentsAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class TasksStudentsActivity : AppCompatActivity() {

    val TAG="TasksStudentsActivity"

    var tareaId : String? = "";
    var groupId : String? = "";
    var isAuthor : Boolean? = false;

    var btnRegresar : ImageView? =  null;

    private lateinit var recyclerViewTasksStudents : RecyclerView
    private lateinit var tasksStudentdAdapter: TasksStudentsAdapter

    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_students)

        this.btnRegresar = findViewById<ImageView>(R.id.btn_tarea_student__regresar)
        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra("tareaId")) {
            val bundle = intent.extras
            this.tareaId = bundle!!.getString("tareaId")
        }

        if (intent.hasExtra("isAuthor")) {
            val bundle = intent.extras
            this.isAuthor = bundle!!.getBoolean("isAuthor")
        }

        if (intent.hasExtra("grupoId")) {
            val bundle = intent.extras
            this.groupId = bundle!!.getString("grupoId")
        }

        this.recyclerViewTasksStudents = findViewById<RecyclerView>(R.id.listTasksStudentsRecyclerView)
        tasksStudentdAdapter = TasksStudentsAdapter(arrayListOf(), tareaId!!, isAuthor!!, groupId!!)
        recyclerViewTasksStudents.apply {
            adapter = tasksStudentdAdapter
            layoutManager = LinearLayoutManager(context)
        }

        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").document(groupId!!).collection("Tareas").document(tareaId!!).collection("TrabajosAlumnos")
            .get()
            .addOnSuccessListener { responseTareas ->

                var arrayTrabajosModel = arrayListOf<TrabajosModel>()

                for (responseTarea in responseTareas) {
                    var trabajoStudent = TrabajosModel()
                    trabajoStudent.id = responseTarea.id
                    trabajoStudent.Documento = if (responseTarea.get("Documento") != null) responseTarea.get("Documento") as String else ""
                    trabajoStudent.TituloDocumento = if (responseTarea.get("TituloDocumento") != null) responseTarea.get("TituloDocumento") as String else ""
                    trabajoStudent.Autor = if (responseTarea.get("Autor") != null) responseTarea.get("Autor") as DocumentReference else null
                    trabajoStudent.FechaEntregada = if (responseTarea.get("FechaEntregada") != null) responseTarea.get("FechaEntregada") as Timestamp else null

                    arrayTrabajosModel.add(trabajoStudent)
                }

                arrayTrabajosModel.sortBy { it.FechaEntregada }

                tasksStudentdAdapter.setListTareas(arrayTrabajosModel)

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo los Grupos", exception)
            }
    }


}
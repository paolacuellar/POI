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
    var isAuthor : Boolean? = false;

    var btnRegresar : ImageView? =  null;

    private lateinit var recyclerViewTasksStudentd : RecyclerView
    private lateinit var tasksStudentdAdapter: TasksStudentsAdapter


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


        this.recyclerViewTasksStudentd = findViewById<RecyclerView>(R.id.listTasksStudentsRecyclerView)
        tasksStudentdAdapter = TasksStudentsAdapter(arrayListOf(), tareaId!!, isAuthor!!)
        recyclerViewTasksStudentd.apply {
            adapter = tasksStudentdAdapter
            layoutManager = LinearLayoutManager(context)
        }

        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        FirebaseFirestore.getInstance().collection("Tareas").document(tareaId!!)
            .get()
            .addOnSuccessListener { responseTrabajo ->

                var arrayTrabajosStudents = if(responseTrabajo.get("Trabajos") != null) responseTrabajo.get("Trabajos") as ArrayList<DocumentReference> else arrayListOf()

                if (arrayTrabajosStudents?.size != 0) {
                    //TODO: PARECE QUE EL "whereIn" NOMAS JALA CON 10 USUARIOS, SI ES ASÍ DEBERE HACER UN CICLO FOR DE CONSULTAS, O DEBERE MANEJARLO DE OTRA MANERA?
                    FirebaseFirestore.getInstance().collection("Trabajos")
                        .whereIn(FieldPath.documentId(), arrayTrabajosStudents!!)
                        .get()
                        .addOnSuccessListener { responseTrabajos ->
                            var arrayTrabajosModel = arrayListOf<TrabajosModel>()
                            for (responseTrabajo in responseTrabajos!!) {
                                var trabajoStudent = TrabajosModel()

                                trabajoStudent.id = responseTrabajo.id

                                trabajoStudent.TituloDocumento = if (responseTrabajo.get("TituloDocumento") != null) responseTrabajo.get("TituloDocumento") as String else ""
                                trabajoStudent.Documento = if (responseTrabajo.get("Documento") != null) responseTrabajo.get("Documento") as String else ""
                                trabajoStudent.Autor = if (responseTrabajo.get("Autor") != null) responseTrabajo.get("Autor") as DocumentReference else null
                                trabajoStudent.FechaEntregada = if (responseTrabajo.get("FechaEntregada") != null) responseTrabajo.get("FechaEntregada") as Timestamp else null

                                arrayTrabajosModel.add(trabajoStudent)
                            }

                            arrayTrabajosModel.sortBy { it.FechaEntregada }

                            tasksStudentdAdapter.setListTareas(arrayTrabajosModel)

                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error al conseguir las Tareas del Grupo", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo los Grupos", exception)
            }
    }


}
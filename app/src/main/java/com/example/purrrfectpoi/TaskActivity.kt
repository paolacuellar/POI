package com.example.purrrfectpoi

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.TrabajosModel
import com.example.purrrfectpoi.adapters.ComentariosAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager

class TaskActivity : AppCompatActivity() {

    var isAuthor : Boolean? = false;
    var tareaId : String? = "";
    var trabajoId : String? = "";

    var trabajoEntregado = TrabajosModel()
    
    var btnRegresar : ImageView? = null;

    var TaskNameText : TextView? = null;
    var TaskDateText : TextView? = null;
    var TaskDescriptionText : TextView? = null;
    
    var AddFileButton : Button? = null;

    private lateinit var recyclerViewFilesTask : RecyclerView
    private lateinit var filesTaskAdapter: ComentariosAdapter

    var SendTaskButton : FloatingActionButton? = null;
    
    private lateinit var db : FirebaseFirestore
    private lateinit var documentReferenceUserLogged : DocumentReference
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        this.recyclerViewFilesTask = findViewById<RecyclerView>(R.id.list_Archive_tasks)

        this.btnRegresar = findViewById<ImageView>(R.id.btn_tarea_regresar)
        this.TaskNameText = findViewById<TextView>(R.id.lbl_tarea_nombre)
        this.TaskDateText = findViewById<TextView>(R.id.lbl_tarea_date)
        this.TaskDescriptionText = findViewById<TextView>(R.id.lbl_tarea_details)
        
        this.AddFileButton = findViewById<Button>(R.id.btnNewArchiveTask)
        this.SendTaskButton = findViewById<FloatingActionButton>(R.id.donetask_btn_floating)

        if (intent.hasExtra("isAuthor")) {
            val bundle = intent.extras
            this.isAuthor = bundle!!.getBoolean("isAuthor")
        }

        if (intent.hasExtra("tareaId")) {
            val bundle = intent.extras
            this.tareaId = bundle!!.getString("tareaId")
        }

        setUpInfoGeneral()

        if (intent.hasExtra("trabajoId")) {
            val bundle = intent.extras
            this.tareaId = bundle!!.getString("trabajoId")

            setUpRecyclerViewTrabajos()
        }

        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }


    }

    private fun setUpInfoGeneral() {
        setViews(this.isAuthor!!)

        setUpInfoTarea()
    }

    
    
    private fun setUpInfoTarea() {

        FirebaseFirestore.getInstance().collection("Tareas").document(this.tareaId!!).get()

            .addOnSuccessListener {
                
                val nombreTarea = if(it.get("Nombre") != null) it.get("Nombre") as String else ""
                val descripcionTarea = if(it.get("Descripcion") != null) it.get("Descripcion") as String else ""
                val fechaProgramadaTarea = if(it.get("FechaProgramada") != null) it.get("FechaProgramada") as Timestamp else null

                TaskNameText?.setText(nombreTarea)
                TaskDescriptionText?.setText(descripcionTarea)
                TaskDateText!!.setText(DataManager.TimeStampToDayHourYear(fechaProgramadaTarea))

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    private fun setUpRecyclerViewTrabajos(){

        FirebaseFirestore.getInstance().collection("Trabajos").document(this.trabajoId!!)
            .get()
            .addOnSuccessListener { responseTrabajo ->
                var arrayTrabajosModel = arrayListOf<TrabajosModel>()


                trabajoEntregado.id = responseTrabajo.id
                trabajoEntregado.Autor = if (responseTrabajo.get("Autor") != null) responseTrabajo.get("Autor") as DocumentReference else null
                trabajoEntregado.FechaEntregada = if (responseTrabajo.get("FechaEntregada") != null) responseTrabajo.get("FechaEntregada") as Timestamp else null
                trabajoEntregado.Documento = if (responseTrabajo.get("Documento") != null) responseTrabajo.get("Documento") as String else ""

                arrayTrabajosModel.add(trabajoEntregado)

                //TODO: HACER //tasksGrupoAdapter.setListTareas(arrayTrabajosModel)

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al conseguir los trabajos", exception)
            }

    }

    private fun setViews(isAuthor: Boolean) {
        if (isAuthor) {
            this.AddFileButton?.visibility = View.GONE
            this.SendTaskButton?.visibility = View.GONE
        }
        else{
            this.AddFileButton?.visibility = View.VISIBLE
            this.SendTaskButton?.visibility = View.VISIBLE

            this.AddFileButton?.setOnClickListener {
                //addFile()
            }

            this.SendTaskButton?.setOnClickListener {
                //sendTaskButton()
            }


        }
    }
}
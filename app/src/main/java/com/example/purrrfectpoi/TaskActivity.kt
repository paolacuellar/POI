package com.example.purrrfectpoi

import android.app.Activity
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.TrabajosModel
import com.example.purrrfectpoi.adapters.TaskFilesAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

class TaskActivity : AppCompatActivity() {

    var isAuthorTeacher : Boolean? = false;
    var tareaId : String? = "";
    var trabajoId : String? = "";
    var groupId : String? = "";
    var fechaProgramadaTarea : Timestamp? = null;

    var filepath : Uri? = null;
    var strFile : String? = null;

    var trabajoEntregado = TrabajosModel()
    
    var btnRegresar : ImageView? = null;

    var TaskNameText : TextView? = null;
    var TaskDateText : TextView? = null;

    var TaskCalificacionText : TextView? = null;

    var TextUsuarioCalificado : TextView? = null;
    var TextUsuarioEmailCalificado : TextView? = null;

    var TaskDescriptionText : TextView? = null;
    
    var AddFileButton : Button? = null;
    var AceptarTareaButton : Button? = null;
    var RechazarTareaButton : Button? = null;

    private lateinit var recyclerViewFilesTask : RecyclerView
    private lateinit var filesTaskAdapter: TaskFilesAdapter

    var SendTaskButton : FloatingActionButton? = null;
    
    private lateinit var db : FirebaseFirestore
    private lateinit var documentReferenceStudentAuthor : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        this.recyclerViewFilesTask = findViewById<RecyclerView>(R.id.list_Archive_tasks)

        this.btnRegresar = findViewById<ImageView>(R.id.btn_tarea_regresar)
        this.TaskNameText = findViewById<TextView>(R.id.lbl_tarea_nombre)
        this.TaskDateText = findViewById<TextView>(R.id.lbl_tarea_date)
        this.TaskDescriptionText = findViewById<TextView>(R.id.lbl_tarea_details)

        this.TaskCalificacionText = findViewById<TextView>(R.id.lbl_calificacion_tarea)

        this.TextUsuarioCalificado = findViewById<TextView>(R.id.lbl_usuario_calificado)
        this.TextUsuarioCalificado?.visibility = View.GONE
        this.TextUsuarioEmailCalificado = findViewById<TextView>(R.id.lbl_correo_usuario_calificado)
        this.TextUsuarioEmailCalificado?.visibility = View.GONE

        this.AddFileButton = findViewById<Button>(R.id.btnNewArchiveTask)
        this.AddFileButton?.visibility = View.GONE

        this.AceptarTareaButton = findViewById<Button>(R.id.btnAcceptTask)
        this.AceptarTareaButton?.visibility = View.GONE
        this.RechazarTareaButton = findViewById<Button>(R.id.btnRejectTask)
        this.RechazarTareaButton?.visibility = View.GONE

        this.SendTaskButton = findViewById<FloatingActionButton>(R.id.donetask_btn_floating)
        this.SendTaskButton?.visibility = View.GONE


        if (intent.hasExtra("isAuthor")) {
            val bundle = intent.extras
            this.isAuthorTeacher = bundle!!.getBoolean("isAuthor")

            if (intent.hasExtra("studentId")) {
                val bundle = intent.extras
                val authorId = bundle!!.getString("studentId")
                documentReferenceStudentAuthor = FirebaseFirestore.getInstance().collection("Usuarios").document(authorId!!)
            }
            else{
                documentReferenceStudentAuthor = FirebaseFirestore.getInstance().collection("Usuarios").document(DataManager.emailUsuario!!)
            }

        }


        if (intent.hasExtra("tareaId")) {
            val bundle = intent.extras
            this.tareaId = bundle!!.getString("tareaId")
        }

        if (intent.hasExtra("grupoId")) {
            val bundle = intent.extras
            this.groupId = bundle!!.getString("grupoId")
        }

        if (intent.hasExtra("trabajoId")) {
            val bundle = intent.extras
            this.trabajoId = bundle!!.getString("trabajoId")
        }

        setUpInfoGeneral()

        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }


    }

    private fun setUpInfoGeneral() {
        var trabajosParam : MutableList<TrabajosModel> = mutableListOf()
        filesTaskAdapter = TaskFilesAdapter(trabajosParam)
        recyclerViewFilesTask.apply {
            adapter = filesTaskAdapter
            layoutManager = LinearLayoutManager(context)
        }
        filesTaskAdapter.setOnItemClickListener(object : TaskFilesAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val manager = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                FirebaseStorage.getInstance().getReference("files/Tareas/${trabajosParam[position].Documento}").downloadUrl
                    .addOnSuccessListener {
                        val request = DownloadManager.Request(it)
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, trabajosParam[position].TituloDocumento)
                        manager.enqueue(request)
                        Toast.makeText(this@TaskActivity, "Archivo descargado", Toast.LENGTH_SHORT).show()
                    }
            }
        })

        setUpInfoTarea()
    }
    
    private fun setUpInfoTarea() {
        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").document(groupId!!).collection("Tareas").document(tareaId!!).get()

            .addOnSuccessListener {
                
                val nombreTarea = if(it.get("Nombre") != null) it.get("Nombre") as String else ""
                val descripcionTarea = if(it.get("Descripcion") != null) it.get("Descripcion") as String else ""
                fechaProgramadaTarea = if(it.get("FechaProgramada") != null) it.get("FechaProgramada") as Timestamp else null

                TaskNameText?.setText(nombreTarea)
                TaskDescriptionText?.setText(descripcionTarea)
                TaskDateText!!.setText("Fecha programada: "+ DataManager.TimeStampToDayHourYear(fechaProgramadaTarea))

                setViews(this.isAuthorTeacher!!)

                setUpRecyclerViewTrabajos()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al conseguir la información del Usuario", exception)
            }
    }

    private fun setUpRecyclerViewTrabajos(){
        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").document(groupId!!).collection("Tareas").document(tareaId!!).collection("TrabajosAlumnos")
            .whereEqualTo("Autor", documentReferenceStudentAuthor)
            .get()
            .addOnSuccessListener { responseTrabajos ->

                for (responseTrabajo in responseTrabajos) {
                    trabajoEntregado.id = responseTrabajo.id
                    trabajoEntregado.Documento = if (responseTrabajo.get("Documento") != null) responseTrabajo.get("Documento") as String else ""
                    trabajoEntregado.TituloDocumento = if(responseTrabajo.get("TituloDocumento") != null) responseTrabajo.get("TituloDocumento") as String else ""
                    trabajoEntregado.Autor = if (responseTrabajo.get("Autor") != null) responseTrabajo.get("Autor") as DocumentReference else null
                    trabajoEntregado.FechaEntregada = if (responseTrabajo.get("FechaEntregada") != null) responseTrabajo.get("FechaEntregada") as Timestamp else null
                    trabajoEntregado.TareaRevisada = if (responseTrabajo.get("TareaRevisada") != null) responseTrabajo.get("TareaRevisada") as Boolean else null

                    this.TextUsuarioEmailCalificado?.text = trabajoEntregado.Autor!!.id

                    filesTaskAdapter.addItem(trabajoEntregado)
                }

                showTaskLabelApprobedReject()

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al conseguir los trabajos", exception)
            }

    }

    private fun addFile() {
        var i = Intent ()
        i.setType("*/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose File"), 333)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 333 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            var index = DocumentFile.fromSingleUri(this, filepath!!)?.type?.indexOf("/")
            var ext = DocumentFile.fromSingleUri(this, filepath!!)?.type?.drop(index!! + 1)
            strFile = UUID.randomUUID().toString() + "." + ext

            trabajoEntregado.TituloDocumento = DocumentFile.fromSingleUri(this, filepath!!)?.name.toString()

            filesTaskAdapter.clearList()
            filesTaskAdapter.addItem(trabajoEntregado)

            SendTaskButton?.visibility = View.VISIBLE
        }
    }

    private fun sendTask() {
        var pathFile = "files/Tareas/${strFile}"
        var fileRef = FirebaseStorage.getInstance().reference.child(pathFile)
        fileRef.putFile(filepath!!)
            .addOnSuccessListener { responseFileUpload ->

                db = FirebaseFirestore.getInstance()
                db.collection("Grupos").document(groupId!!).collection("Tareas").document(tareaId!!).collection("TrabajosAlumnos")
                    .add(
                        hashMapOf(
                            "Documento" to strFile,
                            "TituloDocumento" to DocumentFile.fromSingleUri(this, filepath!!)?.name,
                            "Autor" to documentReferenceStudentAuthor,
                            "FechaEntregada" to FieldValue.serverTimestamp()
                        )
                    )
                    .addOnSuccessListener {

                        if (trabajoEntregado.id.isNotEmpty()) {
                            FirebaseFirestore.getInstance().collection("Grupos").document(groupId!!).collection("Tareas")
                                .document(tareaId!!).collection("TrabajosAlumnos").document(trabajoEntregado.id)
                                .delete()
                            FirebaseStorage.getInstance().reference.child("files/Tareas/${trabajoEntregado.Documento}")
                                .delete()
                        }
                        else {
                            DataManager.updateBadges("Tareas")
                        }

                        db.collection("Grupos").document(groupId!!).collection("Tareas").document(tareaId!!).collection("TrabajosAlumnos").document(it.id)
                            .get()
                            .addOnSuccessListener { responseTrabajo ->

                                trabajoEntregado.id = responseTrabajo.id
                                trabajoEntregado.Documento = if (responseTrabajo.get("Documento") != null) responseTrabajo.get("Documento") as String else ""
                                trabajoEntregado.TituloDocumento = if(responseTrabajo.get("TituloDocumento") != null) responseTrabajo.get("TituloDocumento") as String else ""
                                trabajoEntregado.Autor = if (responseTrabajo.get("Autor") != null) responseTrabajo.get("Autor") as DocumentReference else null
                                trabajoEntregado.FechaEntregada = if (responseTrabajo.get("FechaEntregada") != null) responseTrabajo.get("FechaEntregada") as Timestamp else null

                                Toast.makeText(this, "Tarea enviada", Toast.LENGTH_SHORT).show()

                                SendTaskButton?.visibility = View.GONE

                            }

                    }

            }
    }

    private fun studentCanSendTask(canSendTask : Boolean){
        if (!isAuthorTeacher!!) {
            if (canSendTask) {
                this.AddFileButton?.visibility = View.VISIBLE
                this.SendTaskButton?.visibility = View.VISIBLE

                this.AddFileButton?.setOnClickListener {
                    addFile()
                }

                this.SendTaskButton?.visibility = View.GONE
                this.SendTaskButton?.setOnClickListener {
                    sendTask()
                }
            } else {
                this.AddFileButton?.visibility = View.GONE
                this.SendTaskButton?.visibility = View.GONE
            }
        }
    }

    private fun showTaskLabelApprobedReject(){
        if (trabajoEntregado.TareaRevisada == null && DataManager.getTimeStamptToday().seconds <= fechaProgramadaTarea!!.seconds) {
            this.TaskCalificacionText?.visibility = View.GONE

            studentCanSendTask(true)

        }
        else if (trabajoEntregado.TareaRevisada == null && DataManager.getTimeStamptToday().seconds >= fechaProgramadaTarea!!.seconds){
            this.TaskCalificacionText?.visibility = View.VISIBLE
            this.TaskCalificacionText?.text = "Tarea no entregada"

            studentCanSendTask(false)
        }
        else {
            this.TaskCalificacionText?.visibility = View.VISIBLE
            if (trabajoEntregado.TareaRevisada!!) {
                this.TaskCalificacionText?.text = "Tarea aprobada"
            } else {
                this.TaskCalificacionText?.text = "Tarea reprobada"
            }

            this.AceptarTareaButton?.visibility = View.GONE
            this.RechazarTareaButton?.visibility = View.GONE

            studentCanSendTask(false)
        }
    }

    private fun updateRevisionTask(TareaRevisada : Boolean){
        trabajoEntregado.TareaRevisada = TareaRevisada

        db.collection("Grupos").document(groupId!!).collection("Tareas").document(tareaId!!).collection("TrabajosAlumnos").document(this.trabajoId!!)
            .update(
                mapOf(
                    "TareaRevisada" to  trabajoEntregado.TareaRevisada
                )
            ).addOnCompleteListener{
                if (it.isSuccessful) {
                    showTaskLabelApprobedReject()
                }
                else{
                    DataManager.showAlert(this, "Se ha producido un error al editar el trabajo")
                }
            }
    }

    private fun setViews(isAuthor: Boolean) {
        if (isAuthor) {
            this.AddFileButton?.visibility = View.GONE
            this.SendTaskButton?.visibility = View.GONE

            this.AceptarTareaButton?.visibility = View.VISIBLE
            this.RechazarTareaButton?.visibility = View.VISIBLE

            this.TextUsuarioCalificado?.visibility = View.VISIBLE
            this.TextUsuarioEmailCalificado?.visibility = View.VISIBLE

            this.TaskCalificacionText?.visibility = View.GONE

            this.AceptarTareaButton?.setOnClickListener {
                updateRevisionTask(true)
            }

            this.RechazarTareaButton?.setOnClickListener {
                updateRevisionTask(false)
            }
        }
        else {
            this.AceptarTareaButton?.visibility = View.GONE
            this.RechazarTareaButton?.visibility = View.GONE

            this.TextUsuarioCalificado?.visibility = View.GONE
            this.TextUsuarioEmailCalificado?.visibility = View.GONE
        }
    }
}
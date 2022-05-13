package com.example.purrrfectpoi.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.TrabajosModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.TaskActivity
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import kotlinx.android.synthetic.main.item_task_student.view.*

class TasksStudentsAdapter(val tasksStudents: MutableList<TrabajosModel>, val tareaId: String, var isAuthor: Boolean = false, val groupId: String) : RecyclerView.Adapter<TasksStudentsAdapter.tasksViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tasksViewHolder {
        return tasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task_student, parent, false)
        );
    }

    override fun onBindViewHolder(holder: tasksViewHolder, position: Int) {
       val taskGroup : TrabajosModel = tasksStudents.get(position)

        holder.render(taskGroup, position, tareaId, isAuthor, groupId)
    }

    override fun getItemCount()= tasksStudents.size

    fun setListTareas(arrayTrabajosModel :MutableList<TrabajosModel>){
        for (tareaModel in arrayTrabajosModel) {
            tasksStudents.add(tareaModel)
        }
        this.notifyDataSetChanged()
    }

    class tasksViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun render(taskGroup: TrabajosModel, position: Int, tareaId:String, isAuthor: Boolean, groupId: String){

            view.nameTaskStudent.setText(taskGroup.Autor?.id)
            val dateString : String = "Fecha entregada: " + DataManager.TimeStampToDayHourYear(taskGroup.FechaEntregada!!)
            view.dateTaskStudent.setText(dateString)

            view.nameTaskStudent.setOnClickListener{
                val intent = Intent(view.context, TaskActivity::class.java)
                intent.putExtra("isAuthor", isAuthor)
                intent.putExtra("tareaId", tareaId)
                intent.putExtra("grupoId", groupId)
                view.context?.startActivity(intent)
            }

            if (taskGroup.TareaRevisada == null){
                //TODO: DEJAR CUADRITO VACIO: view.button_tarea_revisada
            }
            else {
                //TODO: SETTEAR PALOMITA O TACHITA
                if (taskGroup.TareaRevisada == null) {
                    //view.button_tarea_revisada = true
                } else if (taskGroup.TareaRevisada == false) {
                    //view.button_tarea_revisada = false
                }
            }

            view.btn_tarea_download.visibility = View.VISIBLE

            view.btn_tarea_download.setOnClickListener{
                FirebaseStorage.getInstance().getReference("files/Tareas/${taskGroup.Documento}").downloadUrl
                    .addOnSuccessListener { fileUri ->
                        val manager = view.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val request = DownloadManager.Request(fileUri)
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, taskGroup.TituloDocumento)
                        manager.enqueue(request)
                        Toast.makeText(view.context, "Archivo descargado", Toast.LENGTH_SHORT).show()
                    }
            }

        }

    }

}
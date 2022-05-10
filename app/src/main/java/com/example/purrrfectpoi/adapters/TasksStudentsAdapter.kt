package com.example.purrrfectpoi.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.TrabajosModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.TaskActivity
import com.psm.hiring.Utils.DataManager
import kotlinx.android.synthetic.main.item_task_student.view.*

class TasksStudentsAdapter(val tasksStudents: MutableList<TrabajosModel>, val tareaId: String, var isAuthor: Boolean = false) : RecyclerView.Adapter<TasksStudentsAdapter.tasksViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tasksViewHolder {
        return tasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task_student, parent, false),
            mListener
        );
    }

    override fun onBindViewHolder(holder: tasksViewHolder, position: Int) {
       
       val taskGroup : TrabajosModel = tasksStudents.get(position)

        holder.render(taskGroup, position, tareaId, isAuthor)
    }

    override fun getItemCount()= tasksStudents.size

    fun setListTareas(arrayTrabajosModel :MutableList<TrabajosModel>){
        for (tareaModel in arrayTrabajosModel) {
            tasksStudents.add(tareaModel)
        }
        this.notifyDataSetChanged()
    }

    class tasksViewHolder(val view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view){
        fun render(taskGroup: TrabajosModel, position: Int, tareaId:String, isAuthor: Boolean){

            view.nameTaskStudent.setText(taskGroup.TituloDocumento)
            val dateString : String = "Fecha entregada: " + DataManager.TimeStampToDayHourYear(taskGroup.FechaEntregada!!)
            view.dateTaskStudent.setText(dateString)

            view.nameTaskStudent.setOnClickListener{
                val intent = Intent(view.context, TaskActivity::class.java)
                intent.putExtra("isAuthor", isAuthor)
                intent.putExtra("tareaId", tareaId)
                intent.putExtra("trabajoId", taskGroup.id)
                view.context?.startActivity(intent)
            }

            if (taskGroup.TareaRevisada == null){
                //TODO: DEJAR CUADRITO VACIO: view.button_tarea_revisada
                view.btn_tarea_download.visibility = View.GONE
            }
            else {
                view.btn_tarea_download.visibility = View.VISIBLE

                view.btn_tarea_download.setOnClickListener{
                    //TODO: DESCARGAR DOCUMENTO
                }

                //TODO: SETTEAR PALOMITA O TACHITA
                if (taskGroup.TareaRevisada == null) {
                    //view.button_tarea_revisada = true
                } else if (taskGroup.TareaRevisada == false) {
                    //view.button_tarea_revisada = false
                }
            }

        }

        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

}
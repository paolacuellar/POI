package com.example.purrrfectpoi.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.AddTaskGroupActivity
import com.example.purrrfectpoi.Models.TareasModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.TaskActivity
import com.example.purrrfectpoi.TasksStudentsActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.psm.hiring.Utils.DataManager
import kotlinx.android.synthetic.main.item_task_group.view.*

class GroupTasksAdapter(val tasksGroup: MutableList<TareasModel>, val groupId: String, var isAuthor: Boolean = false) : RecyclerView.Adapter<GroupTasksAdapter.tasksViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tasksViewHolder {
        return tasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task_group, parent, false),
            mListener
        );
    }

    override fun onBindViewHolder(holder: tasksViewHolder, position: Int) {
       
       val taskGroup : TareasModel = tasksGroup.get(position)

        holder.render(taskGroup, position, groupId, isAuthor)
        /*
        if(buttonVisible){
            holder.view.newChatButton.visibility = View.VISIBLE
        }
        else{
            holder.view.newChatButton.visibility = View.GONE
        }
        */

    }

    override fun getItemCount()= tasksGroup.size

    fun setListTareas(arrayTareasModel :MutableList<TareasModel>){
        for (tareaModel in arrayTareasModel) {
            tasksGroup.add(tareaModel)
        }
        this.notifyDataSetChanged()
    }

    class tasksViewHolder(val view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view){
        fun render(taskGroup: TareasModel, position: Int, groupId:String, isAuthor: Boolean){

            view.nameTaskGroup.setText(taskGroup.Nombre)
            val dateString : String = "Fecha de entrega: " + DataManager.TimeStampToDayHourYear(taskGroup.FechaProgramada!!)
            view.dateTaskGroup.setText(dateString)

            if(!isAuthor) {
                view.btnTaskGroupEdit.visibility = View.GONE
                view.btnTaskGroupDelete.visibility = View.GONE

                view.nameTaskGroup.setOnClickListener{
                    val intent = Intent(view.context, TaskActivity::class.java)
                    intent.putExtra("isAuthor", isAuthor)
                    intent.putExtra("tareaId", taskGroup.id)

                    view.context?.startActivity(intent)
                }
            }
            else{
                view.nameTaskGroup.setOnClickListener{
                    val intent = Intent(view.context, TasksStudentsActivity::class.java)
                    intent.putExtra("tareaId", taskGroup.id)
                    intent.putExtra("isAuthor", isAuthor)
                    view.context?.startActivity(intent)
                }

                if (taskGroup!!.FechaProgramada!!.seconds >= DataManager.getTimeStamptToday().seconds) {
                    view.btnTaskGroupEdit.visibility = View.VISIBLE
                    view.btnTaskGroupEdit.setOnClickListener {
                        val intent = Intent(view.context, AddTaskGroupActivity::class.java)
                        intent.putExtra("grupoId", groupId)
                        intent.putExtra("tareaId", taskGroup.id)
                        view.context?.startActivity(intent)
                    }
                }
                else{
                    view.btnTaskGroupEdit.visibility = View.GONE
                }

                view.btnTaskGroupDelete.visibility = View.VISIBLE
                view.btnTaskGroupDelete.setOnClickListener{
                    //TODO: PENDIENTE BORRAR
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
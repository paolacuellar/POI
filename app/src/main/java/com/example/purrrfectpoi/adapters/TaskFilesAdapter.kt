package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.TrabajosModel
import com.example.purrrfectpoi.R
import kotlinx.android.synthetic.main.item_new_task.view.*

class TaskFilesAdapter(val tasks: MutableList<TrabajosModel>) : RecyclerView.Adapter<TaskFilesAdapter.TaskFilesViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskFilesViewHolder {
        return TaskFilesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_new_task, parent, false),
            mListener
        )
    }

    override fun onBindViewHolder(holder: TaskFilesViewHolder, position: Int) {
        val Task : TrabajosModel = tasks.get(position)

        holder.render(Task, position)
    }

    override fun getItemCount()= tasks.size

    fun addItem(trabajosModel: TrabajosModel){
        if (!this.isItemAdded(trabajosModel.id)) {
            tasks.add(trabajosModel)
            this.notifyDataSetChanged()
        }
    }

    fun isItemAdded(idTask: String) : Boolean{
        var itemAdded = false
        for (index in 0..tasks.count() - 1) {
            if (tasks.get(index).id == idTask) {
                itemAdded = true
                break
            }
        }

        return itemAdded
    }

    fun clearList() {
        tasks.clear()
        this.notifyDataSetChanged()
    }

    class TaskFilesViewHolder(val view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view) {
        fun render(Task: TrabajosModel, position: Int) {

            view.nameArchiveTask.setText(Task.TituloDocumento)

        }

        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}
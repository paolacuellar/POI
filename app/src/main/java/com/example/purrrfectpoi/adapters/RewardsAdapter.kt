package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/*
import com.bumptech.glide.Glide
import com.example.piapoi.ChatActivity
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Models.Rewards
import com.example.piapoi.R


import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.user_chat_recycler.view.*
*/

import com.example.purrrfectpoi.R
class Rewards{

}


class RewardsAdapter(val rewards: MutableList<Rewards>?) : RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder>(){
    class RewardsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsViewHolder {
       return RewardsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_reward,parent,false)
       );
    }

    override fun onBindViewHolder(holder: RewardsViewHolder, position: Int) {
        /*
        val Rewards : Rewards =rewards[position]

        holder.view.messageChat.text= Rewards.message
        holder.view.userNameChat.text=Rewards.name

        if(Rewards.imagePath.equals("Default")){
            Glide.with(holder.view.context)
                .load(Paths.defaultImagePath)
                .into(holder.view.user_image_messages)
        }else{
            FirebaseStorage.getInstance().getReference(Rewards.imagePath).downloadUrl.addOnSuccessListener {
                Glide.with(holder.view.context)
                    .load( it.toString())
                    .into(holder.view.user_image_messages)
            }.addOnCompleteListener{


            }
        }


        holder.view.setOnClickListener{
            val intent = Intent(holder.view.context, ChatActivity::class.java)
            intent.putExtra("uid",Rewards.userUid)
            intent.putExtra("chatName",Rewards.name);
            holder.view.context?.startActivity(intent)
        }
        */
    }

    override fun getItemCount()= rewards!!.size



}
package com.riteshmaagadh.cms.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshmaagadh.cms.R
import com.riteshmaagadh.cms.data.models.Group
import com.riteshmaagadh.cms.databinding.GroupItemBinding


class GroupsAdapter(private val list: List<Group>, val context: Context, private val callbacks: AdapterCallbacks) :
    RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    interface AdapterCallbacks {
        fun onJoinButtonClicked(groupLink: String)
    }

    inner class GroupViewHolder(binding: GroupItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.groupNameTv
        private val joinBtn = binding.button
        val category = binding.textView2
        val imageView = binding.shapeableImageView
        val delete = binding.delete
        val approve = binding.approve

        init {
            joinBtn.setOnClickListener {
                val groupLink = list[adapterPosition].group_link
                callbacks.onJoinButtonClicked(groupLink)
            }

            delete.setOnClickListener {
                FirebaseFirestore.getInstance()
                    .collection("whatsapp_groups")
                    .document(list[adapterPosition].documentId)
                    .delete()
                    .addOnSuccessListener {
                        binding.delete.text = "Deleted"
                    }
            }

            approve.setOnClickListener {
                FirebaseFirestore.getInstance()
                    .collection("whatsapp_groups")
                    .document(list[adapterPosition].documentId)
                    .update("active",true)
                    .addOnSuccessListener {
                        binding.approve.text = "Approved"
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder =
        GroupViewHolder(
            GroupItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.title.text = list[position].title
        holder.category.text = list[position].category
        Glide.with(context)
            .load(list[position].imageUrl)
            .placeholder(R.drawable.picture)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = list.size


}
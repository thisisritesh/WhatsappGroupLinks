package com.riteshmaagadh.cms.data.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.riteshmaagadh.cms.data.models.Category
import com.riteshmaagadh.cms.databinding.CategoryItemBinding
import com.riteshmaagadh.cms.ui.categorydetail.CategoryDetailActivity

class CategoriesAdapter(private val list: List<Category>, val context: Context) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {


    inner class CategoryViewHolder(binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.groupNameTv
        val imageView = binding.imageView

        init {
            binding.root.setOnClickListener {
                val intent = Intent(context, CategoryDetailActivity::class.java)
                intent.putExtra("category_title",list[adapterPosition].title)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
        CategoryViewHolder(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.title.text = list[position].title
        Glide.with(context)
            .load(list[position].image_url)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = list.size


}
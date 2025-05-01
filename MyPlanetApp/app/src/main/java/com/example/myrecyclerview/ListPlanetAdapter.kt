package com.example.myrecyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListPlanetAdapter(private val listPlanets: ArrayList<Planet>, private val isGridMode: Boolean) : RecyclerView.Adapter<ListPlanetAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutId = if (isGridMode) {
            R.layout.item_grid_planet
        } else {
            R.layout.item_row_planet
        }
        val view: View = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listPlanets.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo) = listPlanets[position]
        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name
        holder.tvDescription.text = description

        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            intentDetail.putExtra(DetailActivity.key_planet, listPlanets[holder.adapterPosition])
            holder.itemView.context.startActivity(intentDetail)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Planet)
    }
}
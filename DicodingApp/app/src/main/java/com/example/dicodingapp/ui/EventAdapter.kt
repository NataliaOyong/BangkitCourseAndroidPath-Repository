package com.example.dicodingapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingapp.R
import com.example.dicodingapp.data.response.ListEventsItem

class EventAdapter(
    private var eventList: List<ListEventsItem>,
    private val onItemClick: (ListEventsItem) -> Unit,
    private val useAlternateLayout: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_EVENT = 0
        const val ITEM_EVENT2 = 1
    }

    inner class ViewHolderItemEvent(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        private val eventName: TextView = itemView.findViewById(R.id.eventName)

        fun bind(event: ListEventsItem) {
            eventName.text = event.name
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(eventImage)

            itemView.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    inner class ViewHolderItemEvent2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        private val eventName: TextView = itemView.findViewById(R.id.eventName)

        fun bind(event: ListEventsItem) {
            eventName.text = event.name
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(eventImage)

            itemView.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (useAlternateLayout) ITEM_EVENT2 else ITEM_EVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_EVENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
            ViewHolderItemEvent(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event2, parent, false)
            ViewHolderItemEvent2(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = eventList[position]
        when (holder.itemViewType) {
            ITEM_EVENT -> (holder as ViewHolderItemEvent).bind(event)
            ITEM_EVENT2 -> (holder as ViewHolderItemEvent2).bind(event)
        }
    }

    override fun getItemCount(): Int = eventList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<ListEventsItem>) {
        eventList = newEvents
        notifyDataSetChanged()
    }
}

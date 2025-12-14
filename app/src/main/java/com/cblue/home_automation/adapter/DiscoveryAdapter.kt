package com.cblue.home_automation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cblue.home_automation.R
import com.cblue.home_automation.model.DiscoveryRecord
import kotlin.properties.Delegates

class DiscoveryAdapter : RecyclerView.Adapter<DiscoveryAdapter.DiscoveryViewHolder>() {

    private var onItemClick: ((DiscoveryRecord) -> Unit)? = null

    fun setOnItemClickListener(listener: (DiscoveryRecord) -> Unit) {
        onItemClick = listener
    }

    private fun <T> RecyclerView.Adapter<*>.autoNotify(old: List<T>, new: List<T>, compare: (T, T) -> Boolean) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                compare(old[oldItemPosition], new[newItemPosition])

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition] == new[newItemPosition]

            override fun getOldListSize() = old.size

            override fun getNewListSize() = new.size
        }).dispatchUpdatesTo(this)
    }

    private var items: List<DiscoveryRecord> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { left, right -> left.name == right.name }
    }

  //  private val items = listOf<DiscoveryRecord>(DiscoveryRecord("1212.1212"), DiscoveryRecord("34.2243.3"))
    override fun onBindViewHolder(holder: DiscoveryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveryViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.content_item_discovery, parent, false)
            .let { DiscoveryViewHolder(it) }

    override fun getItemCount(): Int = items.size

    fun addItem(discoveryRecord: DiscoveryRecord) {
        val count = itemCount
        items = items.plus(discoveryRecord)
        notifyItemInserted(count)
    }

    fun removeItem(discoveryRecord: DiscoveryRecord) {
        val count = itemCount
        items = items.minus(discoveryRecord)
        notifyItemRemoved(count)
    }

    fun clear() {
        val count = itemCount
        items = listOf()
        notifyItemRangeRemoved(0, count)
    }

    inner class DiscoveryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val portTextView: TextView = itemView.findViewById(R.id.tv_port)

        fun bind(discoveryRecord: DiscoveryRecord) {
            nameTextView.text = discoveryRecord.name
            portTextView.text = discoveryRecord.address

            itemView.setOnClickListener {
                onItemClick?.invoke(discoveryRecord)
            }
        }

    }

}
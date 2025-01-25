// ConnectionHistoryAdapter.kt
package com.example.nexus

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConnectionHistoryAdapter(
    private val historyList: List<ConnectionHistory>
) : RecyclerView.Adapter<ConnectionHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val connectionName: TextView = itemView.findViewById(R.id.tvConnectionName)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val dataUsed: TextView = itemView.findViewById(R.id.tvDataUsed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_connection_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        Log.d("Adapter", "Binding position: $position, Name: ${history.connectionName}") // Log in onBindViewHolder

        holder.connectionName.text = history.connectionName
        holder.date.text = history.date
        holder.dataUsed.text = history.dataUsed
    }

    override fun getItemCount(): Int = historyList.size
}

package com.itson.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.models.Clase

class ClaseAdapter(
    private val clases: List<Clase>,
    private val onItemClick: (Clase) -> Unit
) : RecyclerView.Adapter<ClaseAdapter.ClaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_clase, parent, false)
        return ClaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClaseViewHolder, position: Int) {
        holder.bind(clases[position], onItemClick)
    }

    override fun getItemCount(): Int = clases.size

    class ClaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombreClase: TextView = itemView.findViewById(R.id.textView_nombreClase)

        fun bind(clase: Clase, onItemClick: (Clase) -> Unit) {
            textViewNombreClase.text = clase.alias
            itemView.setOnClickListener { onItemClick(clase) }
        }
    }
}

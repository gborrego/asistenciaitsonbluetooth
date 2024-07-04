package com.itson.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.models.Clase

class ClaseAdapter(private val clases: List<Clase>) :
    RecyclerView.Adapter<ClaseAdapter.ClaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_clase, parent, false)
        return ClaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClaseViewHolder, position: Int) {
        holder.bind(clases[position])
    }

    override fun getItemCount(): Int = clases.size

    class ClaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombreClase: TextView = itemView.findViewById(R.id.textViewNombreClase)
        private val textViewSimbolo: TextView = itemView.findViewById(R.id.textViewSimbolo)

        fun bind(clase: Clase) {
            textViewNombreClase.text = clase.alias
        }
    }
}
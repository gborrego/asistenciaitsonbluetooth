package com.itson.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.models.Asistencia
import com.itson.models.Clase

class AsistenciaClaseAdapter(
    private val asistenciaList: List<Asistencia>,
    private val onItemClick: (Asistencia) -> Unit
):
    RecyclerView.Adapter<AsistenciaClaseAdapter.AsistenciaClaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaClaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asistencia, parent, false)
        return AsistenciaClaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsistenciaClaseViewHolder, position: Int) {
        holder.bind(asistenciaList[position], onItemClick)
    }

    override fun getItemCount(): Int = asistenciaList.size

    class AsistenciaClaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fechaTextView: TextView = itemView.findViewById(R.id.textView_fecha)
        private val diaTextView: TextView = itemView.findViewById(R.id.text_dia)

        fun bind(asistencia: Asistencia, onItemClick: (Asistencia) -> Unit) {
            fechaTextView.text = asistencia.fecha
            diaTextView.text = "No implementado"
            itemView.setOnClickListener{ onItemClick(asistencia) }
        }
    }
}
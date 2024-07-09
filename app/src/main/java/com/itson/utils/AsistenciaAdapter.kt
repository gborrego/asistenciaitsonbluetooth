package com.itson.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.models.Asistencia

class AsistenciaAdapter(private val attendanceList: List<Asistencia>) :
    RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asistencia, parent, false)
        return AsistenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsistenciaViewHolder, position: Int) {
        holder.bind(attendanceList[position])
    }

    override fun getItemCount(): Int = attendanceList.size

    class AsistenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fechaTextView: TextView = itemView.findViewById(R.id.textViewSimbolo)
        //private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        //private val skipsTextView: TextView = itemView.findViewById(R.id.skipsTextView)

        fun bind(asistencia: Asistencia) {
            fechaTextView.text = asistencia.fecha
          //dayTextView.text = asistencia.f
        }
    }
}
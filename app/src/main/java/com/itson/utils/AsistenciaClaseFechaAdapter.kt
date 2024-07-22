package com.itson.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.models.Asistencia
import com.itson.models.Clase

class AsistenciaClaseFechaAdapter(
    private val asistenciaList: List<Asistencia>,
):
    RecyclerView.Adapter<AsistenciaClaseFechaAdapter.AsistenciaClaseFechaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaClaseFechaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno_asistencia, parent, false)
        return AsistenciaClaseFechaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsistenciaClaseFechaViewHolder, position: Int) {
        holder.bind(asistenciaList[position])
    }

    override fun getItemCount(): Int = asistenciaList.size

    class AsistenciaClaseFechaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.textView_id)
        private val nombreTextView: TextView = itemView.findViewById(R.id.textView_nombre)
        private val asistenciaCheckbox: CheckBox = itemView.findViewById(R.id.checkBox_asistencia)
        private val faltaCheckbox: CheckBox = itemView.findViewById(R.id.checkBox_falta)

        fun bind(asistencia: Asistencia) {
            idTextView.text = asistencia.alumno.matricula.toString()
            nombreTextView.text = asistencia.alumno.nombre
            asistenciaCheckbox.isEnabled = false
            faltaCheckbox.isEnabled=false
            if (asistencia.estado == Asistencia.Estado.PRESENTE){
                asistenciaCheckbox.isChecked = true
            }
        }
    }
}